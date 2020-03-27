// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import java.util.function.Function;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

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
    @SuppressWarnings("unchecked")
    public boolean matches(Object argument)
    {
        return matcher.apply((T) argument);
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText(this.description);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> Matcher<T> matches(Function<T, Boolean> matcher,
                                         String description) {
        return new LambdaMatcher(matcher, description);
    }
}