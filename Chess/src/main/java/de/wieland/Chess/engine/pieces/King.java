package de.wieland.Chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.wieland.Chess.engine.Alliance;
import de.wieland.Chess.engine.board.Board;
import de.wieland.Chess.engine.board.BoardUtils;
import de.wieland.Chess.engine.board.Move;
import de.wieland.Chess.engine.board.Tile;
import de.wieland.Chess.engine.board.Move.MajorAttackMove;
import de.wieland.Chess.engine.board.Move.MajorMove;

/**
 * Public class King.
 * 
 * @author Moritz Wieland
 * @version 1.0
 * @date 10.09.2021
 */
public class King extends Piece {
	private static final int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};
	
	private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

	public King(final Alliance pieceAlliance,
				final int piecePosition,
				final boolean kingSideCastleCapable,
				final boolean queenSideCastleCapable) {
		super(PieceType.KING, piecePosition, pieceAlliance, true);
		this.isCastled = false;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}
	
	public King(final Alliance pieceAlliance,
				final int piecePosition,
				final boolean isFirstMove,
				final boolean isCastled,
				final boolean kingSideCastleCapable,
				final boolean queenSideCastleCapable) {
		super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
		this.isCastled = isCastled;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}

	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		
		for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
			
			if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
			   isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
				continue;
			}
			
			if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				
				if(!candidateDestinationTile.isTileOccupied()) {
					legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
				} else {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAlliance = pieceAtDestination.pieceAlliance;
					
					if (this.pieceAlliance != pieceAlliance) {
						legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
					}
				}
			}
		}
		
		return ImmutableList.copyOf(legalMoves);
	}
	
	@Override
	public int locationBonus() {
		return pieceAlliance.kingBonus(this.piecePosition);
	}
	
	@Override
	public King movePiece(final Move move) {
		return new King(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate(), false, move.isCastlingMove(), false, false);
	}
	
	@Override
	public String toString() {
		return pieceType.toString();
	}
	
	@Override
	public boolean equals(final Object other) {
		if(this == other) {
			return true;
		}
		
		if(!(other instanceof King)) {
			return false;
		}
		
		if(!super.equals(other)) {
			return false;
		}
		
		final King otherKing = (King) other;
		
		return isCastled == otherKing.isCastled;
	}
	
	@Override
	public int hashCode() {
		return (31 * super.hashCode()) + (isCastled ? 1 : 0);
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition,
												  final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9) || (candidateOffset == -1) || (candidateOffset == 7));
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition,
												   final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == -7) || (candidateOffset == 1) || (candidateOffset == 9));
	}
	
	/**
	 * Getter and Setter methods.
	 */
	public boolean isCastled() { return isCastled; }
	public boolean isKingSideCastleCapable() { return this.kingSideCastleCapable; }
    public boolean isQueenSideCastleCapable() { return this.queenSideCastleCapable; }
}
