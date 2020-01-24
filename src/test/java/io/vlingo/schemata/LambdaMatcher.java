package io.vlingo.schemata;

import java.util.function.Function;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.beans.HasPropertyWithValue;

public class LambdaMatcher<T> extends BaseMatcher<T>
{
    private final Function<T, Boolean> matcher;
    private final String description;

    public LambdaMatcher(Function<T, Boolean> matcher,
                         String description)
    {
        this.matcher = matcher;
        this.description = description;
    }

    @Override
    public boolean matches(Object argument)
    {
        return matcher.apply((T) argument);
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText(this.description);
    }

    public static <T> Matcher<T> matches(Function<T, Boolean> matcher,
                                         String description) {
        return new LambdaMatcher(matcher, description);
    }
}