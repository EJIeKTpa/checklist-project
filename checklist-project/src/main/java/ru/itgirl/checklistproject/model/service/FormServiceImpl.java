package ru.itgirl.checklistproject.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itgirl.checklistproject.model.dto.*;
import ru.itgirl.checklistproject.model.entity.Answer;
import ru.itgirl.checklistproject.model.entity.Form;
import ru.itgirl.checklistproject.model.entity.Level;
import ru.itgirl.checklistproject.model.entity.Suggestion;
import ru.itgirl.checklistproject.model.repository.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {
    private final FormRepository formRepository;
    private final AnswerRepository answerRepository;
    private final LevelRepository levelRepository;
    private final QuestionRepository questionRepository;
    private final SuggestionRepository suggestionRepository;

    @Override
    public FormDto createForm(FormCreateDto formCreateDto) {
        Form form = Form.builder()
                .token(formCreateDto.getToken())
                .createdAt(LocalDateTime.now())
                .build();
        return convertEntityToDto(formRepository.save(form));
    }

    @Override
    public FormDto updateForm(FormUpdateDto formUpdateDto) {
        Set<Answer> newAnswers = new HashSet<>();
        List<AnswerCreateDtoForms> answerDtos = formUpdateDto.getAnswers();
        for (AnswerCreateDtoForms answerDto : answerDtos) {
            newAnswers.add(answerRepository.findByTextAndQuestion(answerDto.getAnswerText()
                    , questionRepository.findQuestionByText(answerDto.getQuestion()).orElseThrow(() -> new NoSuchElementException ("This answer does not exist"))).orElseThrow(() -> new NoSuchElementException ("This question does not exist")));
        }

        Form form = formRepository.findByToken(formUpdateDto.getToken()).orElseThrow(() -> new NoSuchElementException ("Form with this token does not exist"));
        Set<Suggestion> suggestions = form.getSuggestions();
        Set<Level> levels = form.getLevels();
        for (Level level : levelRepository.findAll()) {
            List<Answer> answersLevel = newAnswers.stream().filter(answer -> answer.getQuestion().getLevel().equals(level)).collect(Collectors.toList());
            if (!answersLevel.isEmpty()) {
                levels.add(level);
                double correctAnswers = 0;
                for (Answer answer : answersLevel) {
                    if (answer.isCorrect()) {
                        correctAnswers++;
                    }
                }
                if (correctAnswers / answersLevel.size() <= 0.4) {
                    suggestions.addAll(level.getSuggestions());
                } else {
                    suggestions.add(suggestionRepository.findById(1L).orElseThrow(() -> new NoSuchElementException ("This suggestion does not exist")));
                }
            }
        }
        form.setSuggestions(suggestions);
        form.setLevels(levels);
        return convertEntityToDto(formRepository.save(form));
    }

    @Override
    public FormDto updateFormAnswId(FormUpdateDtoAnswId formUpdateDto) {
        Set<Answer> newAnswers = new HashSet<>();
        List<Long> answersId = formUpdateDto.getAnswersId();
        for (Long answerID : answersId) {
            newAnswers.add(answerRepository.findById(answerID).orElseThrow(() -> new NoSuchElementException ("This answer does not exist")));
        }
        Form form = formRepository.findByToken(formUpdateDto.getToken()).orElseThrow(() -> new NoSuchElementException ("Form with this token does not exist"));
        Set<Suggestion> suggestions = form.getSuggestions();
        Set<Level> levels = form.getLevels();
        for (Level level : levelRepository.findAll()) {
            List<Answer> answersLevel = newAnswers.stream().filter(answer -> answer.getQuestion().getLevel().equals(level)).toList();
            if (!answersLevel.isEmpty()) {
                levels.add(level);
                double correctAnswers = 0;
                for (Answer answer : answersLevel) {
                    if (answer.isCorrect()) {
                        correctAnswers++;
                    }
                }
                if (correctAnswers / answersLevel.size() <= 0.4) {
                    suggestions.addAll(level.getSuggestions());
                } else {
                    suggestions.add(suggestionRepository.findById(1L).orElseThrow(() -> new NoSuchElementException ("This suggestion does not exist")));
                }
            }
        }
        form.setSuggestions(suggestions);
        form.setLevels(levels);
        return convertEntityToDto(formRepository.save(form));
    }

    @Override
    public List<FormDto> getAllForms() {
        List<Form> forms = formRepository.findAll();
        return forms.stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    @Override
    public FormDto getFormById(Long id) {
        return convertEntityToDto(formRepository.findById(id).orElseThrow(() -> new NoSuchElementException ("This form does not exist")));
    }

    @Override
    public FormDto getFormByToken(String token) {
        Form form = formRepository.findByToken(token).orElseThrow(() -> new NoSuchElementException ("Form with this token does not exist"));
        return convertEntityToDto(form);
    }

    @Override
    public void deleteForm(Long id) {
        formRepository.deleteById(id);
    }

    private FormDto convertEntityToDto(Form form) {
        List<LevelDtoForm> levelDtos = null;
        List <SuggestionDto> suggestionDtos = null;
        if (form.getLevels() != null) {
            levelDtos = form.getLevels().stream().map(level ->
                    LevelDtoForm.builder()
                            .name(level.getName())
                            .id(level.getId())
                            .build()).collect(Collectors.toList());
        }
        if (form.getSuggestions() != null) {
            suggestionDtos = form.getSuggestions().stream().map(suggestion ->
                    SuggestionDto.builder()
                            .name(suggestion.getName())
                            .link(suggestion.getLink())
                            .build()).collect(Collectors.toList());
        }
        return FormDto.builder()
                .id(form.getId())
                .token(form.getToken())
                .createdAt(form.getCreatedAt().toString())
                .completedLevels(levelDtos)
                .suggestions(suggestionDtos)
                .build();
    }
}
