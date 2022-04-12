package com.xabber.domain.entity;

/**
 * API to create JIDs (XMPP addresses) from Strings and CharSequences.
 * <p>
 * If the input was user generated, e.g. captured from some sort of user
 * interface, {@link #fromUnescaped(String)} should be used instead. This allows
 * the user to enter unescaped JID values. You can use
 * query, e.g. while the user it entering it, if a given CharSequence is a valid
 * bare JID.
 * </p>
 * <p>
 * JIDs created from input received from an XMPP source should use
 * {@link #from(String)}.
 * </p>
 * <p>
 * JidCreate uses caches for efficient Jid construction, But it's not guaranteed
 * that the same String or CharSequence will yield the same Jid instance.
 * </p>
 *
 * @see Jid
 */
public class JidCreate {

    private static final Cache<String, Jid> JID_CACHE = new LruCache<String, Jid>(100);
    private static final Cache<String, BareJid> BAREJID_CACHE = new LruCache<>(100);
    private static final Cache<String, com.xabber.domain.entity.FullJid> FULLJID_CACHE = new LruCache<>(100);
    private static final Cache<String, EntityBareJid> ENTITY_BAREJID_CACHE = new LruCache<>(100);
    private static final Cache<String, EntityFullJid> ENTITY_FULLJID_CACHE = new LruCache<>(100);
    private static final Cache<String, DomainBareJid> DOMAINJID_CACHE = new LruCache<String, DomainBareJid>(100);
    private static final Cache<String, DomainFullJid> DOMAINRESOURCEJID_CACHE = new LruCache<String, DomainFullJid>(100);

    /**
     * Get a {@link Jid} from the given parts.
     * <p>
     * Only the domainpart is required.
     * </p>
     *
     * @param localpart a optional localpart.
     * @param domainpart a required domainpart.
     * @param resource a optional resourcepart.
     * @return a JID which consists of the given parts.
     * @throws XmppStringprepException if an error occurs.
     */
    public static Jid from(CharSequence localpart, CharSequence domainpart, CharSequence resource)
            throws XmppStringprepException {
        return from(localpart.toString(), domainpart.toString(), resource.toString());
    }

    /**
     * Get a {@link Jid} from the given parts.
     * <p>
     * Only the domainpart is required.
     * </p>
     *
     * @param localpart a optional localpart.
     * @param domainpart a required domainpart.
     * @param resource a optional resourcepart.
     * @return a JID which consists of the given parts.
     * @throws XmppStringprepException if an error occurs.
     */
    public static Jid from(String localpart, String domainpart, String resource) throws XmppStringprepException {
        String jidString = XmppStringUtils.completeJidFrom(localpart, domainpart, resource);
        Jid jid = JID_CACHE.lookup(jidString);
        if (jid != null) {
            return jid;
        }
        if (localpart.length() > 0 && domainpart.length() > 0 && resource.length() > 0) {
            jid = new LocalDomainAndResourcepartJid(localpart, domainpart, resource);
        } else if (localpart.length() > 0 && domainpart.length() > 0 && resource.length() == 0) {
            jid = new LocalAndDomainpartJid(localpart, domainpart);
        } else if (localpart.length() == 0 && domainpart.length() > 0 && resource.length() == 0) {
            jid = new DomainpartJid(domainpart);
        } else if (localpart.length() == 0 && domainpart.length() > 0 && resource.length() > 0) {
            jid = new DomainAndResourcepartJid(domainpart, resource);
        } else {
            throw new IllegalArgumentException("Not a valid combination of localpart, domainpart and resource");
        }
        JID_CACHE.put(jidString, jid);
        return jid;
    }

    /**
     * Get a {@link Jid} from a CharSequence.
     *
     * @param jid the input CharSequence.
     * @return the Jid represented by the input CharSequence.
     * @throws XmppStringprepException if an error occurs.
     * @see #from(String)
     */
    public static Jid from(CharSequence jid) throws XmppStringprepException {
        return from(jid.toString());
    }

    /**
     * Get a {@link Jid} from the given String.
     *
     * @param jidString the input String.
     * @return the Jid represented by the input String.
     * @throws XmppStringprepException if an error occurs.
     * @see #from(CharSequence)
     */
    public static Jid from(String jidString) throws XmppStringprepException {
        String localpart = XmppStringUtils.parseLocalpart(jidString);
        String domainpart = XmppStringUtils.parseDomain(jidString);
        String resource = XmppStringUtils.parseResource(jidString);
        try {
            return from(localpart, domainpart, resource);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jidString, e);
        }
    }

    /**
     * Get a {@link Jid} from the given unescaped CharSequence.
     *
     * @param unescapedJid an unescaped CharSequence representing a JID.
     * @return a JID.
     * @throws XmppStringprepException if an error occurs.
     */
    public static Jid fromUnescaped(CharSequence unescapedJid) throws XmppStringprepException {
        return fromUnescaped(unescapedJid.toString());
    }

    /**
     * Get a {@link Jid} from the given unescaped String.
     *
     * @param unescapedJidString a unescaped String representing a JID.
     * @return a JID.
     * @throws XmppStringprepException if an error occurs.
     */
    public static Jid fromUnescaped(String unescapedJidString) throws XmppStringprepException {
        String localpart = XmppStringUtils.parseLocalpart(unescapedJidString);
        // Some as from(String), but we escape the localpart
        localpart = XmppStringUtils.escapeLocalpart(localpart);

        String domainpart = XmppStringUtils.parseDomain(unescapedJidString);
        String resource = XmppStringUtils.parseResource(unescapedJidString);
        try {
            return from(localpart, domainpart, resource);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(unescapedJidString, e);
        }
    }

    /**
     * Get a {@link BareJid} representing the given CharSequence.
     *
     * @param jid the input CharSequence.
     * @return a bare JID representing the given CharSequence.
     * @throws XmppStringprepException if an error occurs.
     */
    public static BareJid bareFrom(CharSequence jid) throws XmppStringprepException {
        return bareFrom(jid.toString());
    }

    /**
     * Get a {@link BareJid} representing the given String.
     *
     * @param jid the input String.
     * @return a bare JID representing the given String.
     * @throws XmppStringprepException if an error occurs.
     */
    public static BareJid bareFrom(String jid) throws XmppStringprepException {
        BareJid bareJid = BAREJID_CACHE.lookup(jid);
        if (bareJid != null) {
            return bareJid;
        }

        String localpart = XmppStringUtils.parseLocalpart(jid);
        String domainpart = XmppStringUtils.parseDomain(jid);
        try {
            if (localpart.length() != 0) {
                bareJid = new LocalAndDomainpartJid(localpart, domainpart);
            } else {
                bareJid = new DomainpartJid(domainpart);
            }
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, e);
        }
        BAREJID_CACHE.put(jid, bareJid);
        return bareJid;
    }

    /**
     * Get a {@link BareJid} constructed from the optionally given {@link Localpart} and {link DomainBareJid}.
     *
     * @param localpart a optional localpart.
     * @param domainBareJid a domain bare JID.
     * @return a bare JID.
     */
    public static BareJid bareFrom(Localpart localpart, DomainBareJid domainBareJid) {
        return bareFrom(localpart, domainBareJid.getDomain());
    }

    /**
     * Get a {@link BareJid} constructed from the optionally given {@link Localpart} and {@link Domainpart}.
     *
     * @param localpart a optional localpart.
     * @param domain a domainpart.
     * @return a bare JID constructed from the given parts.
     */
    public static BareJid bareFrom(Localpart localpart, Domainpart domain) {
        if (localpart != null) {
            return new LocalAndDomainpartJid(localpart, domain);
        } else {
            return new DomainpartJid(domain);
        }
    }

    /**
     * Get a {@link FullJid} representing the given CharSequence.
     *
     * @param jid a CharSequence representing a JID.
     * @return a full JID representing the given CharSequence.
     * @throws XmppStringprepException if an error occurs.
     */
    public static com.xabber.domain.entity.FullJid fullFrom(CharSequence jid) throws XmppStringprepException {
        return fullFrom(jid.toString());
    }

    /**
     * Get a {@link FullJid} representing the given String.
     *
     * @param jid the JID's String.
     * @return a full JID representing the input String.
     * @throws XmppStringprepException if an error occurs.
     */
    public static com.xabber.domain.entity.FullJid fullFrom(String jid) throws XmppStringprepException {
        com.xabber.domain.entity.FullJid fullJid = FULLJID_CACHE.lookup(jid);
        if (fullJid != null) {
            return fullJid;
        }

        String localpart = XmppStringUtils.parseLocalpart(jid);
        String domainpart = XmppStringUtils.parseDomain(jid);
        String resource = XmppStringUtils.parseResource(jid);
        try {
            fullJid = fullFrom(localpart, domainpart, resource);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, e);
        }
        FULLJID_CACHE.put(jid, fullJid);
        return fullJid;
    }

    /**
     * Get a {@link FullJid} constructed from the given parts.
     *
     * @param localpart a optional localpart.
     * @param domainpart a domainpart.
     * @param resource a resourcepart.
     * @return a full JID.
     * @throws XmppStringprepException if an error occurs.
     */
    public static com.xabber.domain.entity.FullJid fullFrom(String localpart, String domainpart, String resource) throws XmppStringprepException {
        com.xabber.domain.entity.FullJid fullJid;
        try {
            if (localpart == null || localpart.length() == 0) {
                fullJid = new DomainAndResourcepartJid(domainpart, resource);
            } else {
                fullJid = new LocalDomainAndResourcepartJid(localpart, domainpart, resource);
            }
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(localpart + '@' + domainpart + '/' + resource, e);
        }
        return fullJid;
    }

    /**
     * Get a {@link FullJid} constructed from the given parts.
     *
     * @param localpart a optional localpart.
     * @param domainBareJid a domain bare JID.
     * @param resource a resourcepart
     * @return a full JID.
     */
    public static com.xabber.domain.entity.FullJid fullFrom(Localpart localpart, DomainBareJid domainBareJid, Resourcepart resource) {
        return fullFrom(localpart, domainBareJid.getDomain(), resource);
    }

    /**
     * Get a {@link FullJid} constructed from the given parts.
     *
     * @param localpart the optional localpart.
     * @param domainpart the domainpart.
     * @param resource the resourcepart.
     * @return a full JID.
     */
    public static com.xabber.domain.entity.FullJid fullFrom(Localpart localpart, Domainpart domainpart, Resourcepart resource) {
        return fullFrom(entityBareFrom(localpart, domainpart), resource);
    }

    /**
     * Get a {@link EntityFullJid} constructed from a {@link EntityBareJid} and a {@link Resourcepart}.
     *
     * @param bareJid a entity bare JID.
     * @param resource a resourcepart.
     * @return a full JID.
     */
    public static EntityFullJid fullFrom(EntityBareJid bareJid, Resourcepart resource) {
        return new LocalDomainAndResourcepartJid(bareJid, resource);
    }

    /**
     * Get a {@link EntityBareJid} representing the given CharSequence.
     *
     * @param jid the input CharSequence.
     * @return a bare JID representing the given CharSequence.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityBareJid entityBareFrom(CharSequence jid) throws XmppStringprepException {
        return entityBareFrom(jid.toString());
    }

    /**
     * Get a {@link EntityBareJid} representing the given String.
     *
     * @param jid the input String.
     * @return a bare JID representing the given String.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityBareJid entityBareFrom(String jid) throws XmppStringprepException {
        EntityBareJid bareJid = ENTITY_BAREJID_CACHE.lookup(jid);
        if (bareJid != null) {
            return bareJid;
        }

        String localpart = XmppStringUtils.parseLocalpart(jid);
        String domainpart = XmppStringUtils.parseDomain(jid);
        try {
            bareJid = new LocalAndDomainpartJid(localpart, domainpart);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, e);
        }
        ENTITY_BAREJID_CACHE.put(jid, bareJid);
        return bareJid;
    }

    /**
     * Get a {@link EntityBareJid} representing the given unescaped CharSequence.
     *
     * @param unescapedJid the input CharSequence.
     * @return a bare JID representing the given CharSequence.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityBareJid entityBareFromUnescaped(CharSequence unescapedJid) throws XmppStringprepException {
        return entityBareFromUnescaped(unescapedJid.toString());
    }

    /**
     * Get a {@link EntityBareJid} representing the given unescaped String.
     *
     * @param unescapedJidString the input String.
     * @return a bare JID representing the given String.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityBareJid entityBareFromUnescaped(String unescapedJidString) throws XmppStringprepException {
        EntityBareJid bareJid = ENTITY_BAREJID_CACHE.lookup(unescapedJidString);
        if (bareJid != null) {
            return bareJid;
        }

        String localpart = XmppStringUtils.parseLocalpart(unescapedJidString);
        // Some as from(String), but we escape the localpart
        localpart = XmppStringUtils.escapeLocalpart(localpart);

        String domainpart = XmppStringUtils.parseDomain(unescapedJidString);
        try {
            bareJid = new LocalAndDomainpartJid(localpart, domainpart);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(unescapedJidString, e);
        }
        ENTITY_BAREJID_CACHE.put(unescapedJidString, bareJid);
        return bareJid;
    }

    /**
     * Get a {@link EntityBareJid} constructed from the given {@link Localpart} and {link DomainBareJid}.
     *
     * @param localpart a localpart.
     * @param domainBareJid a domain bare JID.
     * @return a bare JID.
     */
    public static EntityBareJid entityBareFrom(Localpart localpart, DomainBareJid domainBareJid) {
        return entityBareFrom(localpart, domainBareJid.getDomain());
    }

    /**
     * Get a {@link EntityBareJid} constructed from the given {@link Localpart} and {@link Domainpart}.
     *
     * @param localpart a localpart.
     * @param domain a domainpart.
     * @return a bare JID constructed from the given parts.
     */
    public static EntityBareJid entityBareFrom(Localpart localpart, Domainpart domain) {
        return new LocalAndDomainpartJid(localpart, domain);
    }

    /**
     * Get a {@link EntityFullJid} representing the given CharSequence.
     *
     * @param jid a CharSequence representing a JID.
     * @return a full JID representing the given CharSequence.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityFullJid entityFullFrom(CharSequence jid) throws XmppStringprepException {
        return entityFullFrom(jid.toString());
    }

    /**
     * Get a {@link EntityFullJid} representing the given String.
     *
     * @param jid the JID's String.
     * @return a full JID representing the input String.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityFullJid entityFullFrom(String jid) throws XmppStringprepException {
        EntityFullJid fullJid = ENTITY_FULLJID_CACHE.lookup(jid);
        if (fullJid != null) {
            return fullJid;
        }

        String localpart = XmppStringUtils.parseLocalpart(jid);
        String domainpart = XmppStringUtils.parseDomain(jid);
        String resource = XmppStringUtils.parseResource(jid);
        try {
            fullJid = entityFullFrom(localpart, domainpart, resource);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, e);
        }
        ENTITY_FULLJID_CACHE.put(jid, fullJid);
        return fullJid;
    }

    /**
     * Get a {@link EntityFullJid} representing the given unescaped CharSequence.
     *
     * @param unescapedJid a CharSequence representing a JID.
     * @return a full JID representing the given CharSequence.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityFullJid entityFullFromUnescaped(CharSequence unescapedJid) throws XmppStringprepException {
        return entityFullFromUnescaped(unescapedJid.toString());
    }

    /**
     * Get a {@link EntityFullJid} representing the given unescaped String.
     *
     * @param unescapedJidString the JID's String.
     * @return a full JID representing the input String.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityFullJid entityFullFromUnescaped(String unescapedJidString) throws XmppStringprepException {
        EntityFullJid fullJid = ENTITY_FULLJID_CACHE.lookup(unescapedJidString);
        if (fullJid != null) {
            return fullJid;
        }

        String localpart = XmppStringUtils.parseLocalpart(unescapedJidString);
        // Some as from(String), but we escape the localpart
        localpart = XmppStringUtils.escapeLocalpart(localpart);

        String domainpart = XmppStringUtils.parseDomain(unescapedJidString);
        String resource = XmppStringUtils.parseResource(unescapedJidString);
        try {
            fullJid = new LocalDomainAndResourcepartJid(localpart, domainpart, resource);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(unescapedJidString, e);
        }

        ENTITY_FULLJID_CACHE.put(unescapedJidString, fullJid);
        return fullJid;
    }

    /**
     * Get a {@link EntityFullJid} constructed from the given parts.
     *
     * @param localpart a localpart.
     * @param domainpart a domainpart.
     * @param resource a resourcepart.
     * @return a full JID.
     * @throws XmppStringprepException if an error occurs.
     */
    public static EntityFullJid entityFullFrom(String localpart, String domainpart, String resource) throws XmppStringprepException {
        EntityFullJid fullJid;
        try {
            fullJid = new LocalDomainAndResourcepartJid(localpart, domainpart, resource);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(localpart + '@' + domainpart + '/' + resource, e);
        }
        return fullJid;
    }

    /**
     * Get a {@link EntityFullJid} constructed from the given parts.
     *
     * @param localpart a localpart.
     * @param domainBareJid a domain bare JID..
     * @param resource a resourcepart
     * @return a full JID.
     */
    public static EntityFullJid entityFullFrom(Localpart localpart, DomainBareJid domainBareJid, Resourcepart resource) {
        return entityFullFrom(localpart, domainBareJid.getDomain(), resource);
    }

    /**
     * Get a {@link EntityFullJid} constructed from the given parts.
     *
     * @param localpart the localpart.
     * @param domainpart the domainpart.
     * @param resource the resourcepart.
     * @return a full JID.
     */
    public static EntityFullJid entityFullFrom(Localpart localpart, Domainpart domainpart, Resourcepart resource) {
        return entityFullFrom(entityBareFrom(localpart, domainpart), resource);
    }

    /**
     * Get a {@link EntityFullJid} constructed from a {@link EntityBareJid} and a {@link Resourcepart}.
     *
     * @param bareJid a bare JID.
     * @param resource a resourcepart.
     * @return a full JID.
     */
    public static EntityFullJid entityFullFrom(EntityBareJid bareJid, Resourcepart resource) {
        return new LocalDomainAndResourcepartJid(bareJid, resource);
    }

    /**
     * Deprecated.
     *
     * @param jid the JID.
     * @return a DopmainBareJid
     * @throws XmppStringprepException if an error happens.
     * @deprecated use {@link #domainBareFrom(String)} instead
     */
    @Deprecated
    public static DomainBareJid serverBareFrom(String jid) throws XmppStringprepException {
        return domainBareFrom(jid);
    }

    /**
     * Get a domain bare JID.
     *
     * @param jid the JID CharSequence.
     * @return a domain bare JID.
     * @throws XmppStringprepException if an error occurs.
     */
    public static DomainBareJid domainBareFrom(CharSequence jid) throws XmppStringprepException {
        return domainBareFrom(jid.toString());
    }

    /**
     * Get a domain bare JID.
     *
     * @param jid the JID String.
     * @return a domain bare JID.
     * @throws XmppStringprepException if an error occurs.
     */
    public static DomainBareJid domainBareFrom(String jid) throws XmppStringprepException {
        DomainBareJid domainJid = DOMAINJID_CACHE.lookup(jid);
        if (domainJid != null) {
            return domainJid;
        }

        String domain = XmppStringUtils.parseDomain(jid);
        try {
            domainJid = new DomainpartJid(domain);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, e);
        }
        DOMAINJID_CACHE.put(jid, domainJid);
        return domainJid;
    }

    /**
     * Get a {@link DomainBareJid} consisting of the given {@link Domainpart}.
     *
     * @param domainpart the domainpart.
     * @return a domain bare JID.
     */
    public static DomainBareJid domainBareFrom(Domainpart domainpart) {
        return new DomainpartJid(domainpart);
    }

    /**
     * Deprecated.
     *
     * @param jid the JID.
     * @return a DomainFullJid
     * @throws XmppStringprepException if an error happens.
     * @deprecated use {@link #domainFullFrom(String)} instead
     */
    @Deprecated
    public static DomainFullJid serverFullFrom(String jid) throws XmppStringprepException {
        return donmainFullFrom(jid);
    }

    /**
     * Get a domain full JID from the given String.
     *
     * @param jid the JID.
     * @return a DomainFullJid.
     * @throws XmppStringprepException if an error happens.
     * @deprecated use {@link #domainFullFrom(String)} instead.
     */
    @Deprecated
    public static DomainFullJid donmainFullFrom(String jid) throws XmppStringprepException {
        return domainFullFrom(jid);
    }

    /**
     * Get a domain full JID from the given CharSequence.
     *
     * @param jid the JID.
     * @return a domain full JID.
     * @throws XmppStringprepException if an error happens.
     */
    public static DomainFullJid domainFullFrom(CharSequence jid) throws XmppStringprepException {
        return domainFullFrom(jid.toString());
    }

    /**
     * Get a domain full JID from the given String.
     *
     * @param jid the JID.
     * @return a DomainFullJid.
     * @throws XmppStringprepException if an error happens.
     */
    public static DomainFullJid domainFullFrom(String jid) throws XmppStringprepException {
        DomainFullJid domainResourceJid = DOMAINRESOURCEJID_CACHE.lookup(jid);
        if (domainResourceJid != null) {
            return domainResourceJid;
        }

        String domain = XmppStringUtils.parseDomain(jid);
        String resource = XmppStringUtils.parseResource(jid);
        try {
            domainResourceJid = new DomainAndResourcepartJid(domain, resource);
        } catch (XmppStringprepException e) {
            throw new XmppStringprepException(jid, e);
        }
        DOMAINRESOURCEJID_CACHE.put(jid, domainResourceJid);
        return domainResourceJid;
    }

    /**
     * Get a domain full JID.
     *
     * @param domainpart the domainpart.
     * @param resource the resourcepart.
     * @return a domain full JID.
     */
    public static DomainFullJid domainFullFrom(Domainpart domainpart, Resourcepart resource) {
        return domainFullFrom(domainBareFrom(domainpart), resource);
    }

    /**
     * Get a domain full JID.
     *
     * @param domainBareJid a domain bare JID.
     * @param resource a resourcepart.
     * @return a domain full JID.
     */
    public static DomainFullJid domainFullFrom(DomainBareJid domainBareJid, Resourcepart resource) {
        return new DomainAndResourcepartJid(domainBareJid, resource);
    }
}
