package ru.victortikhonov.autoserviceapp.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.victortikhonov.autoserviceapp.model.Service_Auto_goods.AutoGoodCategory;
import ru.victortikhonov.autoserviceapp.repository.AutoGoodCategoryRepository;


@Component
public class AutoGoodCategoryConverter implements Converter<String, AutoGoodCategory> {

    private final AutoGoodCategoryRepository autoGoodCategoryRepository;


    public AutoGoodCategoryConverter(AutoGoodCategoryRepository autoGoodCategoryRepository) {
        this.autoGoodCategoryRepository = autoGoodCategoryRepository;
    }


    @Override
    public AutoGoodCategory convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        Long categoryId = Long.parseLong(source);

        return autoGoodCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Неревное ID категории автотовара: " + categoryId));
    }
}