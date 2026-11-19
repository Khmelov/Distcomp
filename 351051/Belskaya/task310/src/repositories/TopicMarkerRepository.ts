export class TopicMarkerRepository {
  private topicToMarkers: Map<number, Set<number>> = new Map();
  private markerToTopics: Map<number, Set<number>> = new Map();

  addLink(topicId: number, markerId: number): void {
    if (!this.topicToMarkers.has(topicId)) {
      this.topicToMarkers.set(topicId, new Set());
    }
    this.topicToMarkers.get(topicId)!.add(markerId);

    if (!this.markerToTopics.has(markerId)) {
      this.markerToTopics.set(markerId, new Set());
    }
    this.markerToTopics.get(markerId)!.add(topicId);
  }

  removeLink(topicId: number, markerId: number): void {
    this.topicToMarkers.get(topicId)?.delete(markerId);
    this.markerToTopics.get(markerId)?.delete(topicId);
  }

  getMarkerIdsByTopicId(topicId: number): number[] {
    return Array.from(this.topicToMarkers.get(topicId) ?? []);
  }

  getTopicIdsByMarkerId(markerId: number): number[] {
    return Array.from(this.markerToTopics.get(markerId) ?? []);
  }

  removeTopic(topicId: number): void {
    const markerIds = this.topicToMarkers.get(topicId) ?? new Set();
    for (const markerId of markerIds) {
      this.markerToTopics.get(markerId)?.delete(topicId);
    }
    this.topicToMarkers.delete(topicId);
  }

  removeMarker(markerId: number): void {
    const topicIds = this.markerToTopics.get(markerId) ?? new Set();
    for (const topicId of topicIds) {
      this.topicToMarkers.get(topicId)?.delete(markerId);
    }
    this.markerToTopics.delete(markerId);
  }
}