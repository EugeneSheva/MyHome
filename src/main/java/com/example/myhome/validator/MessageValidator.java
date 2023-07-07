package com.example.myhome.validator;

import com.example.myhome.model.Message;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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
        } else if  (message.getSubject().length()> 100) {
            e.rejectValue("subject", "subject.empty", "Слишком длинный текст");
        }


        if (message.getText() == null ||  message.getText().isEmpty()) {
            e.rejectValue("text", "text.empty", "Заполните поле");
        } else if  (message.getText().length()<2) {
            e.rejectValue("text", "text.empty", "Поле должно быть минимум 2 символа");
        } else if  (message.getText().length()> 200) {
            e.rejectValue("text", "text.empty", "Слишком длинный текст");
        }

    }



}
