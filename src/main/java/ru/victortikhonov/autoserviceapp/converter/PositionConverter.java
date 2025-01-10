package ru.victortikhonov.autoserviceapp.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.victortikhonov.autoserviceapp.model.Personnel.Position;
import ru.victortikhonov.autoserviceapp.repository.PositionRepository;


@Component
public class PositionConverter implements Converter<String, Position> {

    private final PositionRepository positionRepository;


    public PositionConverter(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }


    @Override
    public Position convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        Long categoryId = Long.parseLong(source);
        return positionRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Неревное ID категории услуг: " + categoryId));
    }
}