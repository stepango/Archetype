package com.stepango.archetype.player.data.db.response.feed;


import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Item {

    @Element
    public String title;

    @Namespace(prefix = "content")
    @Element(name = "encoded", required = false)
    @Nullable
    public String content;

    @Namespace(prefix = "itunes")
    @Element(name = "summary")
    public String summary;

    @Namespace(prefix = "itunes")
    @Element(name = "enclosure")
    public Enclosure enclosure;

    @Element(name = "image")
    public Image image;
}
