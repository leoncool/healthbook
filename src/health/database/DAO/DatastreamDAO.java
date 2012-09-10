/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package health.database.DAO;

import health.database.models.Datastream;
import health.database.models.DatastreamBlocks;
import health.database.models.DatastreamUnits;
import health.database.models.JobsTable;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import org.apache.hadoop.hdfs.server.datanode.DataBlockScanner;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import util.AllConstants;
import util.HibernateUtil;

/**
 * 
 * @author Leon
 */
public class DatastreamDAO extends BaseDAO {

	public String createDatastream(Datastream object) {
		try {
			Session session = HibernateUtil.beginTransaction();
			session.save(object);
			HibernateUtil.commitTransaction();
			if (object.getStreamId() != null) {
				return object.getStreamId();
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
		}
	}

	public Datastream getDatastream(String StreamID, boolean fetchDataUnits,
			boolean fetchDatablocks) {
		Datastream stream = null;
		// stream = (Datastream) session.get(Datastream.class, StreamID);
		Session session = HibernateUtil.beginTransaction();
		Criteria criteria = session.createCriteria(Datastream.class);
		if (fetchDataUnits) {
			criteria.setFetchMode("datastreamUnitsList", FetchMode.JOIN);
		}
		if (fetchDatablocks) {
			criteria.setFetchMode("datastreamBlocksList", FetchMode.JOIN);
		}
		stream = (Datastream) criteria.add(Restrictions.idEq(StreamID))
				.uniqueResult();
		HibernateUtil.commitTransaction();
		if (session.isOpen()) {
			session.close();
		}
		return stream;

	}

	public boolean existBlockID_fromDatastream(Datastream stream, String blockID) {
		List<DatastreamBlocks> blockList = stream.getDatastreamBlocksList();
		boolean exist = false;
		for (DatastreamBlocks block : blockList) {
			if (block.getBlockId().equalsIgnoreCase(blockID)) {
				exist = true;
				return exist;
			}
		}
		return exist;
	}

	public List<Datastream> getDatastreamList(Integer SubjectID,
			boolean fetchDataUnits, boolean fetchDatablocks) {
		Session session = HibernateUtil.beginTransaction();
		System.out.println(SubjectID);
		Criteria criteria = session.createCriteria(Datastream.class).add(
				Restrictions.eq("subId", SubjectID));
		// if (fetchDataUnits) {
		// criteria.setFetchMode("datastreamUnitsList", FetchMode.JOIN);
		// }
		// if (fetchDatablocks) {
		// criteria.setFetchMode("datastreamBlocksList", FetchMode.JOIN);
		// }
		List<Datastream> list = criteria.list();
		if (session.isOpen()) {
			session.close();
		}
		return list;
	}

	public List<DatastreamUnits> getDatastreamUnits(String datastreamID) {
		Session session = HibernateUtil.beginTransaction();
		Criteria criteria = session.createCriteria(DatastreamUnits.class).add(
				Restrictions.eq("streamID", datastreamID));
		List<DatastreamUnits> list = criteria.list();
		if (session.isOpen()) {
			session.close();
		}
		return list;
	}

	public Datastream createDatastream(Datastream streamObject,
			List<DatastreamUnits> unitList) {
		try {
			Session session = HibernateUtil.beginTransaction();

			session.save(streamObject);
			for (DatastreamUnits unit : unitList) {
				session.save(unit);
			}
			HibernateUtil.commitTransaction();
			if (streamObject.getStreamId() != null) {
				return streamObject;
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
		}
	}

	public DatastreamBlocks CreateDatastreamBlock(Datastream streamID,
			String blockName, String blockDesc) {
		try {
			Session session = HibernateUtil.beginTransaction();
			DatastreamBlocks block = new DatastreamBlocks();
			UUID uuid = UUID.randomUUID();
			block.setBlockId(uuid.toString());
			// block.setStreamID(streamID);
			Date now = new Date();
			block.setCreated(now);
			block.setDisplayName(blockName);
			block.setStreamID(streamID);
			block.setUpdated(now);
			session.save(block);
			HibernateUtil.commitTransaction();
			return block;
		} catch (Exception ex) {
			HibernateUtil.rollBackTransaction();
			ex.printStackTrace();
			return null;
		}
	}

	public List<DatastreamBlocks> getDatastreamBlockList(String streamID) {
		List objects = null;
		Session session = HibernateUtil.beginTransaction();
		Criteria criteria = session.createCriteria(DatastreamBlocks.class);
		criteria.add(Restrictions.eq("streamID", streamID)).addOrder(
				Order.asc("created"));
		;
		try {
			objects = criteria.list();
			return objects;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	public DatastreamBlocks getDatastreamBlock(String blockID) {
		Session session = HibernateUtil.beginTransaction();
		DatastreamBlocks object = (DatastreamBlocks) session.get(
				DatastreamBlocks.class, blockID);
		try {
			return object;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	public void DeleteDatastream(Datastream datastream) {
		try {
			Session session = HibernateUtil.beginTransaction();
			session.delete(datastream);
			{
				Date now = new Date();
				JobsTable job = new JobsTable();
				job.setCreatedDate(now);
				job.setUpdatedDate(now);
				job.setStatus(AllConstants.ProgramConts.job_status_pending);
				job.setMethod(AllConstants.ProgramConts.job_method_delete);
				job.setTargetObject(AllConstants.ProgramConts.job_targetObject_datastream);
				job.setTargetObjectID(datastream.getStreamId());
				session.save(job);
			}
			HibernateUtil.commitTransaction();
			if (session.isOpen()) {
				session.close();
			}

		} catch (Exception e) {
			HibernateUtil.rollBackTransaction();
			e.printStackTrace();
		}
	}

	public void DeleteDataBlock(DatastreamBlocks block) {
		try {
			Session session = HibernateUtil.beginTransaction();
			session.delete(block);
			{
				Date now = new Date();
				JobsTable job = new JobsTable();
				job.setCreatedDate(now);
				job.setUpdatedDate(now);
				job.setStatus(AllConstants.ProgramConts.job_status_pending);
				job.setMethod(AllConstants.ProgramConts.job_method_delete);
				job.setTargetObject(AllConstants.ProgramConts.job_targetObject_datablock);
				job.setTargetObjectID(block.getBlockId());
				session.save(job);
			}
			HibernateUtil.commitTransaction();
			if (session.isOpen()) {
				session.close();
			}

		} catch (Exception e) {
			HibernateUtil.rollBackTransaction();
			e.printStackTrace();
		}
	}

	public static void main(String arsg[]) {
		DatastreamDAO dao = new DatastreamDAO();
		// dao.CreateDatastreamBlock("leoncool", "blockname1");

	}
}
