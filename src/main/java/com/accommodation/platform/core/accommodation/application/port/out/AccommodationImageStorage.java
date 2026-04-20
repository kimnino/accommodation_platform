package com.accommodation.platform.core.accommodation.application.port.out;

public interface AccommodationImageStorage {

    String upload(byte[] imageData, String fileName);

    void delete(String imageUrl);
}
