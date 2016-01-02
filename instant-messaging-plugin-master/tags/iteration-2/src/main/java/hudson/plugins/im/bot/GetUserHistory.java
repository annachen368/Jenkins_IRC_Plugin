package hudson.plugins.im.bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import hudson.model.AbstractBuild;
import hudson.model.User;

public class GetUserHistory implements Collection<AbstractBuild<?, ?>> {
	
	private Collection<AbstractBuild<?, ?>> filteredBuilds;
	private String username;
	
	public GetUserHistory(Collection<AbstractBuild<?, ?>> builds, String username) {
		this.username = username;
		applyFilter(builds);
		
	}
	
	/**
	 * filter method
	 * It apply filter to received data (builds)
	 */
	private void applyFilter(Collection<AbstractBuild<?, ?>> originals) {
		filteredBuilds = new ArrayList<AbstractBuild<?, ?>>();
		for(AbstractBuild<?, ?> abBuild: originals) {
			if(abBuild.hasParticipant(User.get(username))) {
				filteredBuilds.add(abBuild);
			}
		}
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return filteredBuilds.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return filteredBuilds.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return filteredBuilds.contains(o);
	}

	@Override
	public Iterator<AbstractBuild<?, ?>> iterator() {
		// TODO Auto-generated method stub
		return filteredBuilds.iterator();
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return filteredBuilds.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return filteredBuilds.toArray(a);
	}

	@Override
	public boolean add(AbstractBuild<?, ?> e) {
		// TODO Auto-generated method stub
		return filteredBuilds.add(e);
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return filteredBuilds.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return filteredBuilds.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends AbstractBuild<?, ?>> c) {
		// TODO Auto-generated method stub
		return filteredBuilds.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return filteredBuilds.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return filteredBuilds.retainAll(c);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		filteredBuilds.clear();
	}
}
