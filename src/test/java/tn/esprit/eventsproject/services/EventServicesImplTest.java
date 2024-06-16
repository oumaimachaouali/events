package tn.esprit.eventsproject.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Use MockitoExtension for JUnit 5
class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    @Test
    void addParticipant() {
        // Given
        Participant participant = new Participant();
        when(participantRepository.save(participant)).thenReturn(participant);

        // When
        Participant savedParticipant = eventServices.addParticipant(participant);

        // Then
        assertEquals(participant, savedParticipant);
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void addAffectEvenParticipant() {
        // Given
        Event event = new Event();
        int idParticipant = 1;

        Participant participant = new Participant();
        participant.setIdPart(idParticipant);
        when(participantRepository.findById(idParticipant)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        // When
        Event savedEvent = eventServices.addAffectEvenParticipant(event, idParticipant);

        // Then
        assertEquals(event, savedEvent);
        assertEquals(1, participant.getEvents().size());
        verify(participantRepository, times(1)).findById(idParticipant);
        verify(eventRepository, times(1)).save(event);
    }


    @Test
    void addAffectLog() {
        // Given
        Logistics logistics = new Logistics();
        Event event = new Event();
        String descriptionEvent = "Test Event";
        when(eventRepository.findByDescription(descriptionEvent)).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        // When
        Logistics savedLogistics = eventServices.addAffectLog(logistics, descriptionEvent);

        // Then
        assertEquals(logistics, savedLogistics);
        assertEquals(1, event.getLogistics().size());
        verify(eventRepository, times(1)).findByDescription(descriptionEvent);
        verify(logisticsRepository, times(1)).save(logistics);
    }

    @Test
    void getLogisticsDates() {
        // Given
        LocalDate dateDebut = LocalDate.now().minusDays(1);
        LocalDate dateFin = LocalDate.now().plusDays(1);

        Event event1 = new Event();
        Logistics logistics1 = new Logistics();
        logistics1.setReserve(true);
        logistics1.setPrixUnit(10f);
        logistics1.setQuantite(2);

        Event event2 = new Event();
        Logistics logistics2 = new Logistics(); // This logistics is not reserved

        event1.setLogistics(Collections.singleton(logistics1));
        event2.setLogistics(Collections.singleton(logistics2));

        when(eventRepository.findByDateDebutBetween(dateDebut, dateFin)).thenReturn(Arrays.asList(event1, event2));

        // When
        List<Logistics> result = eventServices.getLogisticsDates(dateDebut, dateFin);

        // Then
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findByDateDebutBetween(dateDebut, dateFin);
    }

    @Test
    void calculCout() {
        // Given
        Event event1 = new Event();
        event1.setDescription("Event 1");
        Logistics logistics1 = new Logistics();
        logistics1.setReserve(true);
        logistics1.setPrixUnit(10f);
        logistics1.setQuantite(2);
        event1.setLogistics(Collections.singleton(logistics1));

        Event event2 = new Event();
        event2.setDescription("Event 2");
        Logistics logistics2 = new Logistics(); // This logistics is not reserved
        event2.setLogistics(Collections.singleton(logistics2));

        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR))
                .thenReturn(Arrays.asList(event1, event2));

        // When
        eventServices.calculCout();

        // Then
        verify(eventRepository, times(1)).findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR);
        verify(eventRepository, times(1)).save(event1);
    }
}
