package com.stepango.archetype.player.data.db.response.feed;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Item {

    @Element
    public String title;

    @Element
    public String description;
}
