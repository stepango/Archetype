package com.stepango.archetype.player.data.db.response.feed;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Image {

    @Attribute(name = "href")
    public String href;
}
