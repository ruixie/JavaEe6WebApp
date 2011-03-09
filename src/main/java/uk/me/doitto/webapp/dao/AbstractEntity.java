/**
 * Copyright (C) 2010 Ian C. Smith <m4r35n357@gmail.com>
 *
 * This file is part of JavaEE6Webapp.
 *
 *     JavaEE6Webapp is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     JavaEE6Webapp is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with JavaEE6Webapp.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.me.doitto.webapp.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlElement;

/**
 * Abstract implementation of PersistentEntity, in which the primary key type
 * parameter is set to Long
 * 
 * @author Ian Smith
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class AbstractEntity implements PersistentEntity<Long>, Comparable<AbstractEntity> {

	/**
	 * For use by persistence provider
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlElement
	protected Long id;

	/**
	 * For use by persistence provider
	 */
	@Version
	@XmlElement
	protected int version;

	/**
	 * for default equals(Object) & hashCode()
	 */
	protected String name;

	private long created;

	private long modified;

	private long accessed;

	protected AbstractEntity () {
		Date date = new Date();
		created = date.getTime();
		modified = date.getTime();
		accessed = date.getTime();
	}

	/**
	 * Copy constructor
	 * @param entity
	 */
	protected AbstractEntity (final AbstractEntity entity) {
		assert entity != null;
		if (! isNew()) {
			this.id = new Long(id.longValue());
		}
		this.version = entity.version;
		this.name = entity.name;
		this.created = entity.created;
		this.modified = entity.modified;
		this.accessed = entity.accessed;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getId () {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNew () {
		return (id == null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getVersion () {
		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName () {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName (final String name) {
		this.name = name;
	}

	@Override
	public Date getAccessed () {
		return new Date(accessed);
	}

	@Override
	public void setAccessed (final Date accessed) {
		this.accessed = accessed.getTime();
	}

	@Override
	public Date getCreated () {
		return new Date(created);
	}

	@Override
	public void setCreated (final Date created) {
		this.created = created.getTime();
	}

	@Override
	public Date getModified () {
		return new Date(modified);
	}

	@Override
	public void setModified (final Date modified) {
		this.modified = modified.getTime();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@Override
	public AbstractEntity deepCopy () throws IOException, ClassNotFoundException {
		ObjectOutputStream oos = null;
		try {
			// Write the object out to a byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.flush();
			oos.close();
			// Make an input stream from the byte array and read a copy of the object back in.
			return getClass().cast(new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject());
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException ex) {
				throw new IOException(ex);
			}
		}
	}

	@Override
	public String toString () {
		return "[" + this.getClass().getSimpleName() + ": " + id + " v" + version + " - " + name + "]";
	}

	@Override
	public int compareTo (final AbstractEntity obj) {
		if (this == obj) {
			return 0;
		}
		return id.compareTo(obj.getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractEntity other = (AbstractEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
