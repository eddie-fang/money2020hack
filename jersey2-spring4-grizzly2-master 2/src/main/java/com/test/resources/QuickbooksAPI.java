package com.test.resources;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.model.Verb;

public class QuickbooksAPI extends DefaultApi10a {

    @Override
    public Verb getRequestTokenVerb()
    {
      return Verb.GET;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return "";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "none";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return "none";
    }
}
