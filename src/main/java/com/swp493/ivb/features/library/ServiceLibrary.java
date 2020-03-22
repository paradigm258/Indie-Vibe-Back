package com.swp493.ivb.features.library;

import java.util.Map;

public interface ServiceLibrary {
    Map<String,Object> getGeneral(String userId,String profileId) throws Exception;
}
