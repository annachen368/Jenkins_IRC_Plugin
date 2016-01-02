package hudson.plugins.im.bot;

import java.util.ArrayList;
import java.util.Collection;

import hudson.model.AbstractBuild;

/**
 * 
 * SuperClass for all BuildsFiler class. Remove unnecessary builds in the
 * collection based on certain criteria
 * 
 * @author Zehao
 *
 */
abstract class AbstractBuildsFilter {
	/**
	 * Remove unnecessary builds in the collection based on certain criteria.
	 * 
	 * @param builds
	 *            The collection of original builds
	 * @param args
	 *            Criteria
	 */
	abstract public void applyFilter(Collection<AbstractBuild<?, ?>> builds, ArrayList<String> args);
}
