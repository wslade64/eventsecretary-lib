package au.com.eventsecretary.render;

/**
 * Given a content identifier and some content, render to a String
 *
 * @author Warwick Slade
 */
public interface Renderer<T>
{
    public String render(String contentIdentifier, T content);
}
