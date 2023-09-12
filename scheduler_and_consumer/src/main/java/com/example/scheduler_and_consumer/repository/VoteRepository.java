package com.example.scheduler_and_consumer.repository;

import com.example.scheduler_and_consumer.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
	@Query("""
			SELECT count(v.userId) as voteCount
			FROM Vote as v
			WHERE (v.userId, v.createdTime) IN (
				SELECT v2.userId, MAX(v2.createdTime)
				FROM Vote v2
				WHERE v2.selectOptionId IN (
					SELECT s.id FROM SelectOption s 
					WHERE s.agenda.id = ( SELECT s2.agenda.id from SelectOption s2 
											WHERE s2.id = :selectOptionId ) 
				)
				GROUP BY v2.userId
			) and v.selectOptionId = :selectOptionId
			GROUP BY v.selectOptionId
			
	""")
	Integer countLastById(Long selectOptionId);
}
