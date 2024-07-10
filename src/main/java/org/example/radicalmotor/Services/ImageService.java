package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Image;
import org.example.radicalmotor.Repositories.IImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class ImageService {
    private final IImageRepository imageRepository;
    public Image getImageByChassisNumber(String chassisNumber) {
        return imageRepository.findByChassisNumber(chassisNumber);
    }
    @Transactional
    public void deleteImage(Image image) {
        if (image != null && image.getId() != null) {
            imageRepository.deleteById(image.getId());
        }
    }

}
