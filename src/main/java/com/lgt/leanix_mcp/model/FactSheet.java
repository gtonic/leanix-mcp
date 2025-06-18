package com.lgt.leanix_mcp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactSheet {
    private String id;
    private String name;
    private String displayName;
    private String fullName;
    private String type;
    private String description;
    private String status;
    private String lxState;
    private Completion completion;
    private String updatedAt;
    private String createdAt;
    private List<Tag> tags;
    private Lifecycle lifecycle;
    private String businessCriticality;
    private String technicalSuitability;
    private String functionalSuitability;
    private Subscriptions subscriptions;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Completion {
        private String completion;
        private Integer percentage;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tag {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Lifecycle {
        private String asString;
        private String phase;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Subscriptions {
        private List<SubscriptionEdge> edges;
        private Integer totalCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubscriptionEdge {
        private Subscription node;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Subscription {
        private String id;
        private String type;
        private User user;
        private List<Role> roles;
        private String createdAt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String id;
        private String displayName;
        private String email;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Role {
        private String id;
        private String name;
        private String comment;
    }
}
