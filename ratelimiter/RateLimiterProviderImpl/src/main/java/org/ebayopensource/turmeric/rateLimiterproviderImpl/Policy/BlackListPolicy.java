/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    
 *******************************************************************************/
package org.ebayopensource.turmeric.rateLimiterproviderImpl.Policy;

import java.util.List;

import org.ebayopensource.turmeric.security.v1.services.IsRateLimitedRequest;
import org.ebayopensource.turmeric.security.v1.services.IsRateLimitedResponse;
import org.ebayopensource.turmeric.security.v1.services.Policy;
import org.ebayopensource.turmeric.security.v1.services.RateLimiterStatus;
import org.ebayopensource.turmeric.security.v1.services.Subject;
import org.ebayopensource.turmeric.security.v1.services.SubjectGroup;
import org.ebayopensource.turmeric.services.ratelimiterservice.impl.RateLimiterException;

/**
 * This class is used to
 * 
 * @author dcarver
 * 
 */
public class BlackListPolicy extends AbstractPolicy {

	private static List<Policy> blacklistPolicy;

	public BlackListPolicy(IsRateLimitedRequest rlRequest) {
		super(rlRequest);

	}

	@Override
	public boolean isExcluded() {
		return false;
	}

	@Override
	public boolean isIncluded() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return blacklistPolicy == null || blacklistPolicy.isEmpty();
	}

	@Override
	public String getPolicyType() {
		return "BLACKLIST";
	}

	// check list against BlackList
	public IsRateLimitedResponse checkBlackList(IsRateLimitedResponse response,
			IsRateLimitedRequest request, List<String> subjects,
			List<String> domain) throws RateLimiterException {
		blacklistPolicy = getPolicies();
		if(blacklistPolicy!=null && !blacklistPolicy.isEmpty()) {
		for (Policy p : blacklistPolicy) {
			if (isPolicySubjectGroupValid(p)) {

				// check SubjectGroup
				for (SubjectGroup subjectGroup : p.getTarget().getSubjects()
						.getSubjectGroup()) {
					if (domain.contains(subjectGroup.getSubjectGroupName()
							.trim())) {
						response.setStatus(RateLimiterStatus.BLOCK);
						return response;
					}

				}
				// check subject
				for (Subject wl : p.getTarget().getSubjects().getSubject()) {
					if (subjects.contains(wl.getSubjectName().trim())) {
						response.setStatus(RateLimiterStatus.BLOCK);
						return response;
					}
				}
				
			}
		}
		}
		return response;
	}

	@Override
	public IsRateLimitedResponse evaluate(IsRateLimitedResponse response,
			IsRateLimitedRequest rlRequest) throws RateLimiterException {
		return checkBlackList(response, rlRequest, super.requestSubjects,
				super.requestSubjectGroups);
	}
}
