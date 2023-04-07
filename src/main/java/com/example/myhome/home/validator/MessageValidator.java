package com.example.myhome.home.validator;

import com.example.myhome.home.model.Message;
import com.example.myhome.home.model.Owner;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
public class MessageValidator implements Validator {


        public boolean supports(Class clazz) {
            return Message.class.equals(clazz);
        }

    @Override
    public void validate(Object obj, Errors e) {
        Message message = (Message) obj;
        if (message.getSubject() == null ||  message.getSubject().isEmpty()) {
            e.rejectValue("subject", "subject.empty", "Заполните поле");
        } else if  (message.getSubject().length()<2) {
            e.rejectValue("subject", "subject.empty", "Поле должно быть минимум 2 символа");
        }
        if (message.getText() == null ||  message.getText().isEmpty()) {
            e.rejectValue("text", "text.empty", "Заполните поле");
        } else if  (message.getText().length()<2) {
            e.rejectValue("text", "text.empty", "Поле должно быть минимум 2 символа");
        }

    }



}
