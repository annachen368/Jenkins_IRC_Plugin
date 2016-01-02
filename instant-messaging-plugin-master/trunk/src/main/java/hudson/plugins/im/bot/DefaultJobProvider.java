package hudson.plugins.im.bot;

import java.util.List;

import hudson.model.AbstractProject;
import hudson.model.View;
import jenkins.model.Jenkins;

/**
 * Default {@link JobProvider} which directly accesses
 * {@link Jenkins#getInstance()}.
 *
 * @author kutzi
 */
public class DefaultJobProvider implements JobProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractProject<?, ?> getJobByName(String name) {
		return Jenkins.getInstance().getItemByFullName(name, AbstractProject.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public AbstractProject<?, ?> getJobByDisplayName(String displayName) {
		List<AbstractProject> allItems = Jenkins.getInstance().getAllItems(AbstractProject.class);
		for (AbstractProject job : allItems) {
			if (displayName.equals(job.getDisplayName())) {
				return job;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractProject<?, ?> getJobByNameOrDisplayName(String name) {
		AbstractProject<?, ?> jobByName = getJobByName(name);
		return jobByName != null ? jobByName : getJobByDisplayName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractProject<?, ?>> getAllJobs() {
		@SuppressWarnings("rawtypes")
		List items = Jenkins.getInstance().getAllItems(AbstractProject.class);
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<AbstractProject<?, ?>> getTopLevelJobs() {
		@SuppressWarnings("rawtypes")
		List items = Jenkins.getInstance().getItems(AbstractProject.class);
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTopLevelJob(AbstractProject<?, ?> job) {
		return Jenkins.getInstance().equals(job.getParent());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(String viewName) {
		return Jenkins.getInstance().getView(viewName);
	}
}
