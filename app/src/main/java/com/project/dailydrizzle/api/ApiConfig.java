package com.project.dailydrizzle.api;


public class ApiConfig {
    //        private static final String BASE_URL = "http://coronahotspots.in/video_app/";
//    private static final String BASE_URL = "http://54.161.190.156/video_app/";
    private static final String BASE_URL = "http://newseeq.com/video_app/";

    public static final String AWS_URL = "https://newseeqvideos.s3-us-west-2.amazonaws.com/";

    public static final String THUMBNAIL_URL = AWS_URL + "thumbs";

    public static final String GET_VIDEOS_OF_CATEGORY = BASE_URL + "api_video.php";
    public static final String GET_VIDEOS_BY_CATEGORY = BASE_URL + "api.php?cat_id=";

    public static final String VIDEO_URL = BASE_URL + "images/animation/";
}