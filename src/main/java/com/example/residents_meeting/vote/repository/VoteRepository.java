package com.example.residents_meeting.vote.repository;

import com.example.residents_meeting.vote.domain.Vote;
import com.example.residents_meeting.vote.domain.dto.VoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
	@Query(""" 
	SELECT v.selectOption.id, v.createdTime
	FROM Vote v
	WHERE v.user.id = :userId
		AND v.selectOption.agenda.id = :agendaId
	ORDER BY v.createdTime DESC
	LIMIT 1
      """)
	VoteHistory findVoteHistoryByUserIdAndAgendaId(Long userId, Long agendaId);
}
