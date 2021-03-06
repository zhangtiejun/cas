package org.apereo.cas.oidc.web.flow;

import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.services.OidcRegisteredService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.services.DefaultRegisteredServiceUserInterfaceInfo;
import org.apereo.cas.web.support.WebUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * This is {@link OidcRegisteredServiceUIActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class OidcRegisteredServiceUIActionTests extends AbstractOidcTests {
    @Autowired
    @Qualifier("oidcRegisteredServiceUIAction")
    private Action oidcRegisteredServiceUIAction;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Test
    public void verifyOidcActionWithoutMDUI() throws Exception {
        final MockRequestContext ctx = new MockRequestContext();
        WebUtils.putService(ctx, null);
        final Event event = oidcRegisteredServiceUIAction.execute(ctx);
        assertEquals("success", event.getId());
        assertNull(WebUtils.getServiceUserInterfaceMetadata(ctx, Serializable.class));
    }

    @Test
    public void verifyOidcActionWithMDUI() throws Exception {
        final OidcRegisteredService svc = new OidcRegisteredService();
        svc.setClientId("id");
        svc.setName("oauth");
        svc.setDescription("description");
        svc.setClientSecret("secret");
        svc.setInformationUrl("info");
        svc.setPrivacyUrl("privacy");
        svc.setServiceId("https://oauth\\.example\\.org.*");
        svc.setLogo("logo");
        servicesManager.save(svc);

        final MockRequestContext ctx = new MockRequestContext();
        WebUtils.putService(ctx, RegisteredServiceTestUtils.getService(
            "https://www.example.org?client_id=id&client_secret=secret&redirect_uri=https://oauth.example.org"));
        final Event event = oidcRegisteredServiceUIAction.execute(ctx);
        assertEquals("success", event.getId());
        final DefaultRegisteredServiceUserInterfaceInfo mdui = WebUtils.getServiceUserInterfaceMetadata(ctx, DefaultRegisteredServiceUserInterfaceInfo.class);
        assertNotNull(mdui);

        assertEquals(mdui.getDisplayName(), svc.getName());
        assertEquals(mdui.getInformationURL(), svc.getInformationUrl());
        assertEquals(mdui.getDescription(), svc.getDescription());
        assertEquals(mdui.getPrivacyStatementURL(), svc.getPrivacyUrl());
        assertEquals(mdui.getLogoUrl(), svc.getLogo());
    }
}
