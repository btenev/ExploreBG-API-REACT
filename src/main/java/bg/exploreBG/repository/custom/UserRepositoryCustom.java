package bg.exploreBG.repository.custom;

import jakarta.persistence.Tuple;

import java.util.stream.Stream;

public interface UserRepositoryCustom {

    Stream<Tuple> getAllUsers();
}
