package com.stepango.archetype.player.db.response.feed;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Item {

    @Element
    public String title;
}
