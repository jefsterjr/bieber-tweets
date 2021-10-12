package org.interview.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class User {

    @SerializedName("id_str")
    private String userId;

    @SerializedName("created_at")
    private Date creationDate;

    @SerializedName("name")
    private String name;

    @SerializedName("screen_name")
    private String screenName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUserId().equals(user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
