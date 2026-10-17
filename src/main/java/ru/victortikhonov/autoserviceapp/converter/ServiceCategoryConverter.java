package ru.victortikhonov.autoserviceapp.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.victortikhonov.autoserviceapp.model.ServiceAndAutoGod.ServiceCategory;
import ru.victortikhonov.autoserviceapp.repository.ServiceCategoryRepository;


@Component
public class ServiceCategoryConverter implements Converter<String, ServiceCategory> {

    private final ServiceCategoryRepository serviceCategoryRepository;



    public ServiceCategoryConverter(ServiceCategoryRepository serviceCategoryRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
    }



    @Override
    public ServiceCategory convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        Long categoryId = Long.parseLong(source);
        return serviceCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Неревное ID категории услуг: " + categoryId));
    }
}