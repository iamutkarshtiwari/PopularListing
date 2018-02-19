package com.iamutkarshtiwari.popularmovies.utils;

/**
 * Created by utkarshtiwari on 25.12.2017.
 */

public class DataUtil {

    public static String extractYearFromDate(String date) {
        String dateArray[] = date.split("-");
        return dateArray[0]; // 0 - index is year
    }

    public static String formatMovieDetailsVoteAverage(Double voteAverage) {
        return Double.toString(voteAverage) + "/10";
    }
}
