package net.minecraft.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class Session {
    public String username;
    public String playerID;
    public String token;
    /**
     * -- GETTER --
     * Returns either 'legacy' or 'mojang' whether the account is migrated or not
     */
    private final Type sessionType;

    public Session(String usernameIn, String playerIDIn, String tokenIn, String sessionTypeIn) {
        this.username = usernameIn;
        this.playerID = playerIDIn;
        this.token = tokenIn;
        this.sessionType = Type.setSessionType(sessionTypeIn);
    }

    @JsonIgnore
    public String getSessionID() {
        return "token:" + this.token + ":" + this.playerID;
    }

    @JsonIgnore
    public GameProfile getProfile() {
        try {
            UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
            return new GameProfile(uuid, this.getUsername());
        } catch (IllegalArgumentException var2) {
            return new GameProfile((UUID) null, this.getUsername());
        }
    }

    public Session() {

        sessionType = Type.LEGACY;
    }

    public static enum Type {
        LEGACY("legacy"),
        MOJANG("mojang");

        private static final Map<String, Type> SESSION_TYPES = Maps.<String, Type>newHashMap();
        private final String sessionType;

        private Type(String sessionTypeIn) {
            this.sessionType = sessionTypeIn;
        }

        public static Type setSessionType(String sessionTypeIn) {
            return (Type) SESSION_TYPES.get(sessionTypeIn.toLowerCase());
        }

        static {
            for (Type session$type : values()) {
                SESSION_TYPES.put(session$type.sessionType, session$type);
            }
        }
    }
}
