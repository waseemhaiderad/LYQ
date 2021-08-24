package lnq.com.lnq.model.event_bus_models;

import java.util.Collection;

import lnq.com.lnq.model.userprofile.SocialMediaLinksModel;

public class EventBusSocialMedia {
    private String socialLink;


    public EventBusSocialMedia(String socialLink) {
        this.socialLink = socialLink;
    }

    public String getSocialLink() {
        return socialLink;
    }

    public void setSocialLink(String socialLink) {
        this.socialLink = socialLink;
    }

}
