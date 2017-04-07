package com.stepango.archetype.player.data.db.response.feed;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
public class Channel {

    @Element
    public String title;

    @ElementList(inline = true)
    public List<Item> item;
}
