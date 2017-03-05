package gameEngine.audioEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dferreira.commons.Vector3f;

import gameEngine.models.AudioBuffer;
import gameEngine.models.AudioSource;
import gameEngine.models.Player;
import gameEngine.models.complexEntities.Entity;
import gameEngine.models.complexEntities.TEntity;

/**
 * Is going to demand the reproduction of audio due to the control of the
 * entities
 */
public class EntityPlayer extends GenericPlayer {

	/**
	 * If the entity is far away from the player more that the threshold is not
	 * going to be listened
	 */
	private float SOUND_THRESHOLD = 50f;

	/**
	 * First if going to make a rough approximation only after is going to check
	 * if exists a crash
	 */
	private float ROUGH_THRESHOLD = 10;
	private float CRASH_THRESHOLD = 2;

	/**
	 * List of sound sources available to the entity player
	 * 
	 */
	private List<AudioSource> sourcesAvailable;

	/**
	 * The player can always play audio;
	 */
	private AudioSource playerSource;

	/**
	 * Sources assign
	 */
	private Map<Entity, AudioSource> sourcesAssign;

	/**
	 * Constructor of the entity player
	 */
	public EntityPlayer(List<AudioSource> sourcesAvailable) {
		this.sourcesAvailable = sourcesAvailable;
		if ((this.sourcesAvailable != null) && (!this.sourcesAvailable.isEmpty())) {
			playerSource = this.sourcesAvailable.remove(this.sourcesAvailable.size() - 1);
		}
		this.sourcesAssign = new HashMap<Entity, AudioSource>();
	}


	/**
	 * Set the listener position and reproduces the sound of the player
	 * 
	 * @param library
	 *            Set of sounds that can use to provide sound feedback to the
	 *            user
	 * @param player
	 *            The player of the scene and also the listener
	 */
	private void playPlayer(HashMap<TAudioEnum, AudioBuffer> library, Player player) {
		this.positioningListener(player.getPosition());

		// If the player is running plays the sound of the footsteps
		if (player.getCurrentSpeed() == 0) {
			if (this.isPlaying(this.playerSource)) {
				this.pause(this.playerSource);
			}
		} else {
			float pitch = player.getCurrentSpeed() / 30.0f;
			if (this.isPaused(this.playerSource)) {
				this.setPitch(this.playerSource, pitch);
				this.continuePlaying(this.playerSource);
			} else {
				if (!this.isPlaying(this.playerSource)) {
					AudioBuffer footStepsBuffer = library.get(TAudioEnum.footsteps);
					this.setPitch(this.playerSource, pitch);
					this.setVolume(this.playerSource, 1.0f);
					this.play(playerSource, footStepsBuffer);
					this.setLoop(this.playerSource, true);
				}
			}
			this.setPosition(this.playerSource, player.getPosition());
		}
	}

	/**
	 * 
	 * @param position1
	 *            The position one to take in account
	 * @param position2
	 *            The position two entity to take in account
	 * @param threshold
	 *            The threshold that is going to use to the match
	 * 
	 * @return False = Not passed in the rough approximation True = Passed in
	 *         the rough approximation
	 */
	private boolean roughApproximation(Vector3f position1, Vector3f position2, float threshold) {
		return (Math.abs(position1.x - position2.x) < threshold) && 
				(Math.abs(position1.y - position2.y) < threshold) && 
				(Math.abs(position1.z - position2.z) < threshold);
	}

	/**
	 * 
	 * @param position1
	 *            The position one to take in account
	 * @param position2
	 *            The position two entity to take in account
	 * 
	 * 
	 * @return The square of the distance between two positions
	 */
	private float squareDistance(Vector3f position1, Vector3f position2) {
		float xDiff = Math.abs(position1.x - position2.x);
		float yDiff = Math.abs(position1.y - position2.y);
		float zDiff = Math.abs(position1.z - position2.z);

		return (xDiff * xDiff) + (yDiff * yDiff) + (zDiff * zDiff);
	}

	/**
	 * 
	 * @param pPosition
	 *            The position of player to take in account
	 * @param ePosition
	 *            The position of entity to take in account
	 * 
	 * @return False the player can not listen the entity True the entity can be
	 *         listen by the player
	 */
	private boolean passesThreshold(Vector3f pPosition, Vector3f ePosition) {
		return roughApproximation(pPosition, ePosition, SOUND_THRESHOLD);
	}

	/**
	 * Assign audio sources to the entities
	 * 
	 * @param player
	 *            The player of the scene and also the listener
	 * @param entities
	 *            Set of entities that should provide sound feedback
	 */
	private void assignSources(Player player, Entity[] entities) {
		if ((player != null) && (entities != null)) {
			Vector3f pPosition = player.getPosition();
			for (Entity entity : entities) {
				if (passesThreshold(pPosition, entity.getPosition())) {
					// Can not assign nothing
					if (sourcesAvailable.isEmpty()) {
						continue;
					}
					// Check is a source was already assign
					if (!sourcesAssign.containsKey(entity)) {
						AudioSource aSource = sourcesAvailable.remove(sourcesAvailable.size() - 1);
						sourcesAssign.put(entity, aSource);
					}
				} else {
					// Not passed threshold
					if (sourcesAssign.containsKey(entity)) {
						AudioSource aSource = sourcesAssign.remove(entity);
						this.stop(aSource);
						this.sourcesAvailable.add(aSource);
					}
				}
			}
		}
	}

	/**
	 * Check is an entity crashed against the player
	 * 
	 * @param library
	 *            Set of sounds that can use to provide sound feedback to the
	 *            user
	 * @param entity
	 *            The entity to check
	 * @param player
	 *            The position of the player that is going to check the crash
	 * 
	 * @return False = No crash was detected True = A crash was detected and the
	 *         sound was played
	 */
	private boolean playCrash(HashMap<TAudioEnum, AudioBuffer> library, Entity entity, Vector3f player) {
		if (roughApproximation(player, entity.getPosition(), ROUGH_THRESHOLD)
				&& (squareDistance(player, entity.getPosition()) < CRASH_THRESHOLD)) {
			AudioSource source = sourcesAssign.get(entity);
			TEntity tEntity = entity.getGenericEntity().getObjectType(); 
			switch(tEntity) {
			case banana_tree:
				break;
			case fern:
				break;
			case flower:
				break;
			case grass:
				break;
			case marble:
				if (!isPlaying(source) && (source.getBuffer() == null)) {
					AudioBuffer breakingWood = library.get(TAudioEnum.breakingWood);
					this.setPitch(source, 1.0f);
					this.setVolume(source, 1.0f);
					this.setPosition(source, entity.getPosition());
					this.play(source, breakingWood);
					this.setLoop(source, false);
				}
				break;
			case player:
				break;
			case tree:
				AudioBuffer breakingWood = library.get(TAudioEnum.breakingWood);
				if (!isPlaying(source) || ((source.getBuffer() == null) || (source.getBuffer() != breakingWood))) {
					this.setPitch(source, 1.0f);
					this.setVolume(source, 1.0f);
					this.setPosition(source, entity.getPosition());
					this.play(source, breakingWood);
					this.setLoop(source, false);
				}
				break;
			default:
				break;
			}
			
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Reproduces the sound of the entities
	 * 
	 * @param library
	 *            Set of sounds that can use to provide sound feedback to the
	 *            user
	 * @param entities
	 *            Set of entities that should provide sound feedback
	 * @param player
	 *            The player of the scene and also the listener
	 */
	private void playEntities(HashMap<TAudioEnum, AudioBuffer> library, Set<Entity> entities, Player player) {
		if (entities != null) {
			Vector3f pPosition = player.getPosition();

			for (Entity entity : entities) {
				// Check is has one source
				AudioSource source = sourcesAssign.get(entity);
				if (source == null) {
					continue;
				}

				if (!playCrash(library, entity, pPosition)) {
					// If not played a crash
					if (entity.getGenericEntity().getObjectType() != TEntity.tree) {
						continue;
					}

					// Make birds to play in each tree
					AudioBuffer breakingWood = library.get(TAudioEnum.breakingWood);
					if(source.getBuffer() == breakingWood) {
						continue;
					}
					
					if (!isPlaying(source)) {
						AudioBuffer falconBuffer = library.get(TAudioEnum.falcon);
						this.setPitch(source, 1.0f);
						this.setVolume(source, 1.0f);
						this.setPosition(source, entity.getPosition());
						this.play(source, falconBuffer);
						this.setLoop(source, true);
					}
				}

			}
		}
	}

	/**
	 * Plays all the sounds related with entities
	 * 
	 * @param library
	 *            Set of sounds that can use to provide sound feedback to the
	 *            user
	 * @param entities
	 *            Set of entities that should provide sound feedback
	 * @param player
	 *            The player of the scene and also the listener
	 */
	public void play(HashMap<TAudioEnum, AudioBuffer> library, Entity[] entities, Player player) {
		if (player == null) {
			return;
		}
		this.playPlayer(library, player);
		this.assignSources(player, entities);
		this.playEntities(library, this.sourcesAssign.keySet(), player);

	}

	/**
	 * Clean up because we need to clean up when we finish the program
	 */
	public void cleanUp() {

	}
}
