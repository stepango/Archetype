package com.stepango.archetype.player.db.response.feed;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Rss {

    @Element
    public Channel channel;
}
