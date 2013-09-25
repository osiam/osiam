package org.osiam.security.authorization;

import org.osiam.storage.dao.ClientDao;
import org.osiam.storage.entities.ClientEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;

/**
 * Osiam user approval handler extends the default user approval handler from spring.
 * It will add an implicit approval if configured in the client, so that the user is never asked to approve the client.
 * Additionally an expiry period can be configured if the implicit is not desired and the user need to approve once.
 * After that he will be asked again only if the period expires
 */
@Component("userApprovalHandler")
public class OsiamUserApprovalHandler extends DefaultUserApprovalHandler {

    @Inject
    private ClientDao clientDao;

    private static final int MILLISECONDS = 1000;

    /**
     * Is called if OsiamUserApprovalHandler.isApproved() returns false and AccessConfirmation is done by the user.
     * Than it will save the approve date to be able to check it as long as user accepts approval.
     * So the user is not bothered every time to approve the client.
     *
     * @param authorizationRequest spring authorizationRequest
     * @param userAuthentication spring userAuthentication
     * @return the authorizationRequest
     */
    @Override
    public AuthorizationRequest updateBeforeApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        //check if "user_oauth_approval" is in the authorizationRequests approvalParameters and the (size != 0)
        // -> true for accessConfirmation -> save actual date
        if (authorizationRequest.getApprovalParameters().size() != 0 && authorizationRequest.getApprovalParameters().containsKey("user_oauth_approval")
                && authorizationRequest.getApprovalParameters().get("user_oauth_approval").equals("true")) {
            ClientEntity client = getClientDetails(authorizationRequest);
            Date date = new Date(System.currentTimeMillis() + (client.getValidityInSeconds() * MILLISECONDS));
            client.setExpiry(date);
            clientDao.update(client, authorizationRequest.getClientId());
        }
        return super.updateBeforeApproval(authorizationRequest, userAuthentication);
    }

    /**
     * Checks if the client is configured to not ask the user for approval or if the date to ask again expires.
     *
     * @param authorizationRequest spring authorizationRequest
     * @param userAuthentication spring userAuthentication
     * @return whether user approved the client or not
     */
    @Override
    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        //check if implicit is configured in client or if user already confirmed approval once and validity time is not over
        ClientEntity client = getClientDetails(authorizationRequest);
        if (client.isImplicit()) {
            return true;
        } else if (client.getExpiry() != null && client.getExpiry().compareTo(new Date(System.currentTimeMillis())) >= 0) {
            return true;
        }
        return false;
    }

    private ClientEntity getClientDetails(AuthorizationRequest authorizationRequest) {
        return clientDao.getClient(authorizationRequest.getClientId());
    }
}