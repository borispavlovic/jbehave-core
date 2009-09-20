package org.jbehave.scenario.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jbehave.scenario.RunnableScenario;
import org.jbehave.scenario.definition.StoryDefinition;
import org.jbehave.scenario.errors.InvalidScenarioResourceException;
import org.jbehave.scenario.errors.ScenarioNotFoundException;

/**
 * Loads scenario definition from classpath resources, which are handled by the
 * {@link ScenarioParser}. Names of resources are resolved via the
 * {@link ScenarioNameResolver}.
 */
public class ClasspathScenarioDefiner implements ScenarioDefiner {

    private final ScenarioNameResolver resolver;
    private final ScenarioParser parser;
    private final ClassLoader classLoader;

    public ClasspathScenarioDefiner() {
        this(new UnderscoredCamelCaseResolver(), new PatternScenarioParser(), Thread.currentThread()
                .getContextClassLoader());
    }

    public ClasspathScenarioDefiner(ScenarioParser parser) {
        this(new UnderscoredCamelCaseResolver(), parser, Thread.currentThread().getContextClassLoader());
    }

    public ClasspathScenarioDefiner(ScenarioNameResolver converter, ScenarioParser parser) {
        this(converter, parser, Thread.currentThread().getContextClassLoader());
    }

    public ClasspathScenarioDefiner(ScenarioNameResolver converter, ClassLoader classLoader) {
        this(converter, new PatternScenarioParser(), classLoader);
    }

    public ClasspathScenarioDefiner(ScenarioNameResolver resolver, ScenarioParser parser, ClassLoader classLoader) {
        this.resolver = resolver;
        this.parser = parser;
        this.classLoader = classLoader;
    }

    public StoryDefinition loadScenarioDefinitionsFor(Class<? extends RunnableScenario> scenarioClass) {
        String wholeFileAsString = asString(loadInputStreamFor(scenarioClass));
        return parser.defineStoryFrom(wholeFileAsString);
    }

	public StoryDefinition loadScenarioDefinitionsFor(String scenarioPath) {
        String wholeFileAsString = asString(loadInputStreamFor(scenarioPath));
        return parser.defineStoryFrom(wholeFileAsString);
	}

    private InputStream loadInputStreamFor(Class<? extends RunnableScenario> scenarioClass) {
        return loadInputStreamFor(resolver.resolve(scenarioClass));
    }

	private InputStream loadInputStreamFor(String scenarioPath) {
		InputStream stream = classLoader.getResourceAsStream(scenarioPath);
        if (stream == null) {
            throw new ScenarioNotFoundException("Scenario " + scenarioPath + " could not be found by classloader "
                    + classLoader);
        }
        return stream;
	}

    private String asString(InputStream stream) {
        try {
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(bytes);
            return output.toString();
        } catch (IOException e) {
            throw new InvalidScenarioResourceException("Failed to convert scenario resource to string", e);
        }
    }

}