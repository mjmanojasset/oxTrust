/*
 * oxTrust is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package org.gluu.oxtrust.service.antlr.scimFilter.visitor.scim2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.gluu.oxtrust.model.scim2.BaseScimResource;
import org.gluu.oxtrust.model.scim2.group.GroupResource;
import org.gluu.oxtrust.model.scim2.Meta;
import org.gluu.oxtrust.model.scim2.user.Group;
import org.gluu.oxtrust.service.antlr.scimFilter.MainScimFilterVisitor;
import org.gluu.oxtrust.service.antlr.scimFilter.antlr4.ScimFilterParser;
import org.gluu.oxtrust.service.antlr.scimFilter.enums.ScimOperator;
import org.gluu.oxtrust.service.antlr.scimFilter.util.FilterUtil;
import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Val Pecaoco
 */
public class GroupFilterVisitor extends MainScimFilterVisitor {

    private Logger logger = LoggerFactory.getLogger(GroupFilterVisitor.class);

    private String attrOperCriteriaResolver(String attrName, String operator, String criteria) {

        logger.info(" GroupFilterVisitor.attrOperCriteriaResolver() ");

        attrName = FilterUtil.stripScim2Schema(attrName);

        String[] tokens = attrName.split("\\.");

        String ldapAttributeName = getLdapAttributeName(attrName, GroupResource.class);

        criteria = evaluateMultivaluedCriteria(criteria, operator, tokens);

        logger.info(" ##### attrName = " + attrName + ", ldapAttributeName = " + ldapAttributeName + ", criteria = " + criteria);

        String expr = ScimOperator.transform(operator, ldapAttributeName, criteria);
        logger.info(" ##### expr = " + expr);

        return expr;
    }

    /**
     * {@InheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public String visitATTR_OPER_CRITERIA(ScimFilterParser.ATTR_OPER_CRITERIAContext ctx) {

        logger.info(" GroupFilterVisitor.visitATTR_OPER_CRITERIA() ");

        ParseTree parent = ctx.getParent();
        while (parent != null) {

            if (parent.getClass().getSimpleName().equalsIgnoreCase(ScimFilterParser.LBRAC_EXPR_RBRACContext.class.getSimpleName())) {

                logger.info("********** PARENT = " + parent.getClass().getSimpleName());

                String attrName = ((ScimFilterParser.LBRAC_EXPR_RBRACContext)parent).ATTRNAME() + "." + ctx.ATTRNAME().getText();
                return attrOperCriteriaResolver(attrName, visit(ctx.operator()), visit(ctx.criteria()));

            } else {
                parent = parent.getParent();
            }
        }

        return attrOperCriteriaResolver(ctx.ATTRNAME().getText(), visit(ctx.operator()), visit(ctx.criteria()));
    }

    /**
     * {@InheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public String visitATTR_OPER_EXPR(ScimFilterParser.ATTR_OPER_EXPRContext ctx) {

        logger.info(" GroupFilterVisitor.visitATTR_OPER_EXPR() ");

        ParseTree parent = ctx.getParent();
        while (parent != null) {

            if (parent.getClass().getSimpleName().equalsIgnoreCase(ScimFilterParser.LBRAC_EXPR_RBRACContext.class.getSimpleName())) {

                logger.info("********** PARENT = " + parent.getClass().getSimpleName());

                String attrName = ((ScimFilterParser.LBRAC_EXPR_RBRACContext)parent).ATTRNAME() + "." + ctx.ATTRNAME().getText();
                return attrOperCriteriaResolver(attrName, visit(ctx.operator()), visit(ctx.expression()));

            } else {
                parent = parent.getParent();
            }
        }

        return attrOperCriteriaResolver(ctx.ATTRNAME().getText(), visit(ctx.operator()), visit(ctx.expression()));
    }

    /**
     * {@InheritDoc}
     * <p>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     *
     * @param ctx
     */
    @Override
    public String visitATTR_PR(ScimFilterParser.ATTR_PRContext ctx) {

        logger.info(" GroupFilterVisitor.visitATTR_PR() ");

        String attrName = ctx.ATTRNAME().getText();

        ParseTree parent = ctx.getParent();
        while (parent != null) {

            if (parent.getClass().getSimpleName().equalsIgnoreCase(ScimFilterParser.LBRAC_EXPR_RBRACContext.class.getSimpleName())) {

                logger.info("********** PARENT = " + parent.getClass().getSimpleName());

                attrName = ((ScimFilterParser.LBRAC_EXPR_RBRACContext)parent).ATTRNAME() + "." + ctx.ATTRNAME().getText();
                break;

            } else {
                parent = parent.getParent();
            }
        }

        attrName = FilterUtil.stripScim2Schema(attrName);
        String[] tokens = attrName.split("\\.");

        String ldapAttributeName = getLdapAttributeName(attrName, GroupResource.class);

        logger.info(" ##### ATTRNAME = " + ctx.ATTRNAME().getText() + ", ldapAttributeName = " + ldapAttributeName);

        String expr = evaluateIsPresentCriteria(ldapAttributeName, tokens);
        logger.info(" ##### expr = " + expr);

        return expr;
    }
}
