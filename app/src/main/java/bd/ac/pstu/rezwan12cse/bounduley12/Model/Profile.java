/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package bd.ac.pstu.rezwan12cse.bounduley12.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Profile implements Serializable {

    private String id;
    public String Ausername;
    public String email;
    public String CphotoUrl;
    public String Bphoneno;
    public String timestamp;
    public double latitude;
    public double longitude;
    private long likesCount;
    private String registrationToken;
    public String CurrentStatus;



    public Profile() {
        // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    }


    public String getStatus() {
        return CurrentStatus;
    }

    public void setStatus(String status) {
        this.CurrentStatus = status;
    }
    public Profile(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return Ausername;
    }

    public void setUsername(String Ausername) {
        this.Ausername = Ausername;
    }


    public String getPhoneno() {
        return Bphoneno;
    }

    public void setPhoneno(String Bphoneno) {
        this.Bphoneno = Bphoneno;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }




    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return CphotoUrl;
    }

    public void setPhotoUrl(String CphotoUrl) {
        this.CphotoUrl = CphotoUrl;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }
}
