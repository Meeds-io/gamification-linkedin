package io.meeds.linkedin.gamification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoteOrganization implements Cloneable {

    private long                 id;
    private String               vanityName;
    private String               localizedName;

    @Override
    public RemoteOrganization clone() {
        return new RemoteOrganization(id, vanityName, localizedName);
    }
}
