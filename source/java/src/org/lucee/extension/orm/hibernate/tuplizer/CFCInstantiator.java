package org.opencfmlfoundation.extension.orm.hibernate.tuplizer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.Instantiator;
import org.opencfmlfoundation.extension.orm.hibernate.CommonUtil;
import org.opencfmlfoundation.extension.orm.hibernate.HibernateCaster;
import org.opencfmlfoundation.extension.orm.hibernate.HibernateORMEngine;
import org.opencfmlfoundation.extension.orm.hibernate.HibernateORMSession;
import org.opencfmlfoundation.extension.orm.hibernate.HibernatePageException;
import org.opencfmlfoundation.extension.orm.hibernate.HibernateUtil;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public class CFCInstantiator implements Instantiator {
	
	private String entityName;
	private Set<String> isInstanceEntityNames = new HashSet<String>();
	
	public CFCInstantiator() {
		this.entityName = null;
	}

	/**
	 * Constructor of the class
	 * @param mappingInfo
	 */
	public CFCInstantiator(PersistentClass mappingInfo) {
		this.entityName = mappingInfo.getEntityName();
		isInstanceEntityNames.add( entityName );
		if ( mappingInfo.hasSubclasses() ) {
			Iterator<PersistentClass> itr = mappingInfo.getSubclassClosureIterator();
			while ( itr.hasNext() ) {
				final PersistentClass subclassInfo = itr.next();
				isInstanceEntityNames.add( subclassInfo.getEntityName() );
			}
		}
	}

	@Override
	public final Object instantiate(Serializable id) {
		return instantiate();
	}

	@Override
	public final Object instantiate() {
		try {
			PageContext pc = CommonUtil.pc();
			HibernateORMSession session=HibernateUtil.getORMSession(pc,true);
			HibernateORMEngine engine=(HibernateORMEngine) session.getEngine();
			Component c = engine.create(pc, session, entityName, true);
			c.setEntity(true);
			return c;//new CFCProxy(c);
		} 
		catch (PageException pe) {
			throw new HibernatePageException(pe);
		}
	}

	@Override
	public final boolean isInstance(Object object) {
		Component cfc = CommonUtil.toComponent(object,null);
		if(cfc==null) return false;
		return isInstanceEntityNames.contains( HibernateCaster.getEntityName(cfc));
	}
}