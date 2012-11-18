package de.shop.artikelverwaltung.domain;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;


@RunWith(Arquillian.class)