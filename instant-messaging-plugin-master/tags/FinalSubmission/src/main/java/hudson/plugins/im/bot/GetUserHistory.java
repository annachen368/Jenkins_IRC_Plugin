package hudson.plugins.im.bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import hudson.model.AbstractBuild;
import hudson.model.User;

/**
 * Returns collection of builds started by a user.
 * 
 * @author BookReaders CS 427 Group UIUC
 */
public class GetUserHistory implements Collection<AbstractBuild<?, ?>> {

	private Collection<AbstractBuild<?, ?>> filteredBuilds;
	private String username;

	/**
	 * Constructor.
	 * 
	 * @param builds
	 *            Builds that filter will be applied to to find the user's
	 * @param username
	 *            User's name to find builds for
	 */
	public GetUserHistory(Collection<AbstractBuild<?, ?>> builds, String username) {
		this.username = username;
		applyFilter(builds);

	}

	private void applyFilter(Collection<AbstractBuild<?, ?>> originals) {
		filteredBuilds = new ArrayList<AbstractBuild<?, ?>>();
		for (AbstractBuild<?, ?> abBuild : originals) {
			if (abBuild.hasParticipant(User.get(username))) {
				filteredBuilds.add(abBuild);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return filteredBuilds.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return filteredBuilds.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object o) {
		return filteredBuilds.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<AbstractBuild<?, ?>> iterator() {
		return filteredBuilds.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return filteredBuilds.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return filteredBuilds.toArray(a);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(AbstractBuild<?, ?> e) {
		return filteredBuilds.add(e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object o) {
		return filteredBuilds.remove(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return filteredBuilds.containsAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends AbstractBuild<?, ?>> c) {
		return filteredBuilds.addAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return filteredBuilds.removeAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return filteredBuilds.retainAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		filteredBuilds.clear();
	}
}
