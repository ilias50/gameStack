package com.gamestack.collection.dto;

public class GameDto {

    private Long apiId;
    private String title;
    private String platform;
    private String releaseDate;
    private String imagePath;
    private long platform_id;

    // Constructeurs
    public GameDto() {
    }

    public GameDto(Long apiId, String title, String platform, String releaseDate, String imagePath,  long platform_id) {
        this.apiId = apiId;
        this.title = title;
        this.platform = platform;
        this.releaseDate = releaseDate;
        this.imagePath = imagePath;
        this.platform_id = platform_id;
    }

    // Getters et Setters
    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getPlatform_id() {
        return platform_id;
    }

    public void setPlatform_id(int platform_id) {
        this.platform_id = platform_id;
    }
}
