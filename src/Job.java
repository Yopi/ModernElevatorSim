/*
 * Job class, this is a job for an elevator.
 * 
 * @author: Viktor Björkholm & Jesper Bränn
 * @version: 2015-04-15
 */

public class Job {
		
		public int from;	// From what node the job origins
		public int to;		// To what node the job is finished.
		public int id;		// The person that called for the job.
		
					// The job does not store path since this might change
					// during the job if new jobs are added.
				
		public Job(int from, int to, int id) {
			this.from = from;
			this.to = to;
			this.id = id;
		}
	}