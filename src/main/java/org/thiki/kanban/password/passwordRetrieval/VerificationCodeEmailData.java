package org.thiki.kanban.password.passwordRetrieval;

import org.thiki.kanban.foundation.mail.MailEntity;

/**
 * Created by xubt on 8/14/16.
 */
public class VerificationCodeEmailData extends MailEntity {
    private String verificationCode;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Override
    public String getSubject() {
        return "密码找回";
    }
}
