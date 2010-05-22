package org.jbehave.ant;

import static org.apache.tools.ant.Project.MSG_INFO;

import java.util.List;

import org.apache.tools.ant.BuildException;
import org.jbehave.core.RunnableStory;
import org.jbehave.core.StoryEmbedder;

/**
 * Ant task that runs stories
 * 
 * @author Mauro Talevi
 */
public class StoryRunnerTask extends AbstractStoryTask {

    public void execute() throws BuildException {
        StoryEmbedder embedder = newStoryEmbedder();
        List<RunnableStory> stories = stories();
		log("Running stories "+stories+" using embedder "+embedder, MSG_INFO);
		embedder.runStories(stories);
    }

}