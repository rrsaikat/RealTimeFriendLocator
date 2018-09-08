package bd.ac.pstu.rezwan12cse.bounduley12.utils;


import java.util.Date;

import bd.ac.pstu.rezwan12cse.bounduley12.enums.UploadImagePrefix;


public class ImageUtil {

    public static String generateImageTitle(UploadImagePrefix prefix, String parentId) {
        if (parentId != null) {
            return prefix.toString() + parentId;
        }

        return prefix.toString() + new Date().getTime();
    }
}
