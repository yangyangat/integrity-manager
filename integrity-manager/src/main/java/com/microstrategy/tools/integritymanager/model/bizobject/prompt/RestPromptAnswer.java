package com.microstrategy.tools.integritymanager.model.bizobject.prompt;

import java.util.List;

public class RestPromptAnswer {
    private List<AnswerPromptData> prompts;

    public RestPromptAnswer(List<AnswerPromptData> prompts) {
        this.prompts = prompts;
    }

    public RestPromptAnswer() {
    }

    public List<AnswerPromptData> getPrompts() {
        return this.prompts;
    }

    public void setPrompts(List<AnswerPromptData> prompts) {
        this.prompts = prompts;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof RestPromptAnswer)) return false;
        final RestPromptAnswer other = (RestPromptAnswer) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$prompts = this.getPrompts();
        final Object other$prompts = other.getPrompts();
        if (this$prompts == null ? other$prompts != null : !this$prompts.equals(other$prompts)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof RestPromptAnswer;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $prompts = this.getPrompts();
        result = result * PRIME + ($prompts == null ? 43 : $prompts.hashCode());
        return result;
    }

    public String toString() {
        return "RestPromptAnswer(prompts=" + this.getPrompts() + ")";
    }
}
