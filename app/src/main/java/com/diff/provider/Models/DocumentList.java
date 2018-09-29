package com.diff.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by santhosh@appoets.com on 11-09-2018.
 */
public class DocumentList {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("provider_id")
    @Expose
    private Integer providerId;
    @SerializedName("document_id")
    @Expose
    private String documentId;
    @SerializedName("url")
    @Expose
    private Object url;
    @SerializedName("image_front")
    @Expose
    private String imageFront;
    @SerializedName("image_back")
    @Expose
    private String imageBack;
    @SerializedName("passport")
    @Expose
    private Object passport;
    @SerializedName("unique_id")
    @Expose
    private Object uniqueId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("expires_at")
    @Expose
    private Object expiresAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("document")
    @Expose
    private Document document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public String getImageFront() {
        return imageFront;
    }

    public void setImageFront(String imageFront) {
        this.imageFront = imageFront;
    }

    public String getImageBack() {
        return imageBack;
    }

    public void setImageBack(String imageBack) {
        this.imageBack = imageBack;
    }

    public Object getPassport() {
        return passport;
    }

    public void setPassport(Object passport) {
        this.passport = passport;
    }

    public Object getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Object uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Object expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public class Document {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("front")
        @Expose
        private Integer front;
        @SerializedName("back")
        @Expose
        private Integer back;
        @SerializedName("image")
        @Expose
        private Integer image;
        @SerializedName("passport")
        @Expose
        private Integer passport;
        @SerializedName("expiry_date")
        @Expose
        private Integer expiryDate;
        @SerializedName("type")
        @Expose
        private String type;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getFront() {
            return front;
        }

        public void setFront(Integer front) {
            this.front = front;
        }

        public Integer getBack() {
            return back;
        }

        public void setBack(Integer back) {
            this.back = back;
        }

        public Integer getImage() {
            return image;
        }

        public void setImage(Integer image) {
            this.image = image;
        }

        public Integer getPassport() {
            return passport;
        }

        public void setPassport(Integer passport) {
            this.passport = passport;
        }

        public Integer getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(Integer expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
}
