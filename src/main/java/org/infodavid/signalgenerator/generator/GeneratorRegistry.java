package org.infodavid.signalgenerator.generator;

import java.rmi.NoSuchObjectException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GeneratorRegistry.
 */
public final class GeneratorRegistry {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorRegistry.class);

    /** The Constant SINGLETON. */
    private static final GeneratorRegistry SINGLETON = new GeneratorRegistry();

    /**
     * Gets the single instance.
     * @return single instance
     */
    public static GeneratorRegistry getInstance() {
        return SINGLETON;
    }

    /** The generators. */
    private final Map<String, Class<? extends Generator>> generators; // NOSONAR

    /**
     * Instantiates a new registry.
     */
    private GeneratorRegistry() { // NOSONAR
        generators = new HashMap<>();
        final ServiceLoader<Generator> loader = ServiceLoader.load(Generator.class);
        final Iterator<Generator> ite = loader.iterator();

        while (ite.hasNext()) {
            final Generator generator = ite.next();
            generators.put(generator.getName(), generator.getClass());
            LOGGER.info("Implementation {} installed for {}", generator.getClass().getName(), generator.getName());
        }

        if (generators.isEmpty()) {
            LOGGER.warn("No implementation found");
        }
    }

    /**
     * Gets the generator.
     * @param name the name
     * @return the generator
     * @throws NoSuchObjectException the no such object exception
     */
    public Generator getGenerator(final String name) throws NoSuchObjectException { // NOSONAR
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name is null or empty");
        }

        try {
            return generators.get(name).newInstance();
        } catch (@SuppressWarnings("unused") InstantiationException | IllegalAccessException e) {
            throw new NoSuchObjectException(name);
        }
    }

    /**
     * Gets the supported generators.
     * @return the supported generators
     */
    public Collection<Class<? extends Generator>> getGenerators() {
        return generators.values();
    }
}
