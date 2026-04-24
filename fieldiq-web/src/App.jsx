import { useState, useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from 'react-leaflet'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl:       'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl:     'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

function stopIcon(n, done) {
  return L.divIcon({
    className: '',
    html: `<div style="background:${done ? '#10b981' : '#6366f1'};color:#fff;width:28px;height:28px;
      border-radius:50%;display:flex;align-items:center;justify-content:center;
      font-weight:700;font-size:13px;border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3)">
      ${done ? '✓' : n}</div>`,
    iconSize: [28, 28], iconAnchor: [14, 14],
  })
}

const homeIcon = L.divIcon({
  className: '',
  html: `<div style="background:#f59e0b;color:#fff;width:32px;height:32px;border-radius:8px;
    display:flex;align-items:center;justify-content:center;font-size:16px;
    border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3)">🏠</div>`,
  iconSize: [32, 32], iconAnchor: [16, 16],
})

function FitBounds({ points }) {
  const map = useMap()
  useEffect(() => {
    if (points.length > 1) map.fitBounds(points, { padding: [40, 40] })
    else if (points.length === 1) map.setView(points[0], 14)
  }, [points.length])
  return null
}

const API = 'http://localhost:8080'

const AVATAR_COLORS = ['#6366f1','#10b981','#f59e0b','#ef4444','#8b5cf6','#0ea5e9']

function initials(name) {
  return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase()
}

function statusChip(status) {
  const map = {
    PENDING:     ['chip chip-pending',  'Pending'],
    IN_PROGRESS: ['chip chip-progress', 'In Progress'],
    COMPLETED:   ['chip chip-done',     'Done'],
    SKIPPED:     ['chip chip-skipped',  'Skipped'],
  }
  const [cls, label] = map[status] || ['chip', status]
  return <span className={cls}>{label}</span>
}

function fmt(amount) {
  if (!amount) return '—'
  return '₹' + Number(amount).toLocaleString('en-IN')
}

function Spinner({ text = 'Loading…' }) {
  return (
    <div className="loading-wrap">
      <div className="spinner" />
      <div className="loading-text">{text}</div>
    </div>
  )
}

// ─── Screen 1: Agent Selection ────────────────────────────────────────────────

function AgentSelect({ onSelect }) {
  const [agents, setAgents] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch(`${API}/api/agents`)
      .then(r => r.json())
      .then(data => { setAgents(data); setLoading(false) })
      .catch(() => setLoading(false))
  }, [])

  return (
    <div className="screen">
      <div className="topbar">
        <div className="topbar-row">
          <div className="app-logo">⚡</div>
          <div>
            <div className="topbar-title">FieldIQ</div>
            <div className="topbar-sub">Collections Intelligence</div>
          </div>
        </div>
      </div>

      <div className="content">
        <div className="hero">
          <h2>Good morning 👋</h2>
          <p>Select your profile to view today's route and assignments.</p>
        </div>

        <div className="section-label">YOUR TEAM</div>

        {loading && <Spinner text="Loading agents…" />}

        {agents.map((agent, i) => (
          <div key={agent.id} className="card" onClick={() => onSelect(agent)}>
            <div className="agent-card-inner">
              <div className="agent-avatar" style={{ background: AVATAR_COLORS[i % AVATAR_COLORS.length] }}>
                {initials(agent.name)}
              </div>
              <div style={{ flex: 1 }}>
                <div className="agent-name">{agent.name}</div>
                <div className="agent-code">{agent.agentCode} · {agent.phone}</div>
              </div>
              <div className="agent-right">
                <div className="agent-zone">{agent.zone}</div>
                <div className="arrow-icon">›</div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

// ─── Screen 2: Today's Route ──────────────────────────────────────────────────

function RouteList({ agent, preloadedVisits, onVisitClick, onBack }) {
  const [visits, setVisits]   = useState(preloadedVisits ?? [])
  const [loading, setLoading] = useState(preloadedVisits === null)
  const [generating, setGen]  = useState(false)
  const [view, setView]       = useState('list')
  const today = new Date().toISOString().split('T')[0]

  const load = () => {
    setLoading(true)
    fetch(`${API}/api/routes/agent/${agent.id}?date=${today}`)
      .then(r => r.json())
      .then(data => { setVisits(Array.isArray(data) ? data : []); setLoading(false) })
      .catch(() => setLoading(false))
  }

  const generateRoutes = () => {
    setGen(true)
    fetch(`${API}/api/routes/generate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ date: today }),
    })
      .then(r => r.json())
      .then(() => { setGen(false); load() })
      .catch(() => setGen(false))
  }

  useEffect(() => {
    if (preloadedVisits !== null) {
      setVisits(preloadedVisits)
      setLoading(false)
    } else {
      load()
    }
  }, [agent.id])

  const done      = visits.filter(v => v.status === 'COMPLETED').length
  const total     = visits.length
  const totalDist = visits.reduce((s, v) => s + (v.distanceFromPrevious || 0), 0).toFixed(1)
  const totalMins = visits.reduce((s, v) => s + (v.estimatedTravelMinutes || 0), 0)

  const homePos   = [agent.homeLatitude, agent.homeLongitude]
  const stopPoints = visits.map(v => [v.latitude, v.longitude])
  const routeLine  = [homePos, ...stopPoints].filter(p => p[0] && p[1])

  return (
    <div className="screen">
      <div className="topbar">
        <div className="topbar-row">
          <button className="back-btn" onClick={onBack}>‹</button>
          <div style={{ flex: 1 }}>
            <div className="topbar-title">{agent.name}</div>
            <div className="topbar-sub">{agent.zone} · {today}</div>
          </div>
        </div>
      </div>

      {loading && <Spinner text="Loading route…" />}

      {!loading && total === 0 && (
        <div className="content">
          <div className="empty-wrap">
            <div className="empty-icon">🗓</div>
            <div className="empty-title">No visits scheduled</div>
            <div className="empty-sub">No route has been generated for today yet. Tap below to generate optimised routes.</div>
            <button className="btn btn-primary" onClick={generateRoutes} disabled={generating}>
              {generating ? 'Generating…' : '⚡ Generate Today\'s Routes'}
            </button>
          </div>
        </div>
      )}

      {!loading && total > 0 && (
        <>
          <div className="content" style={{ paddingBottom: 0 }}>
            <div className="stats-row">
              <div className="stat-box">
                <div className="stat-val" style={{ color: '#6d28d9' }}>{done}/{total}</div>
                <div className="stat-lbl" style={{ color: '#7c3aed' }}>Visits</div>
              </div>
              <div className="stat-box">
                <div className="stat-val" style={{ color: '#1d4ed8' }}>{totalDist}</div>
                <div className="stat-lbl" style={{ color: '#1d4ed8' }}>km total</div>
              </div>
              <div className="stat-box">
                <div className="stat-val" style={{ color: '#166534' }}>{Math.round(totalMins / 60 * 10) / 10}</div>
                <div className="stat-lbl" style={{ color: '#166534' }}>hrs est.</div>
              </div>
            </div>

            <div className="toggle-tabs">
              {['map', 'list'].map(t => (
                <button
                  key={t}
                  className={`toggle-tab${view === t ? ' active' : ''}`}
                  onClick={() => setView(t)}
                >
                  {t === 'map' ? '🗺 Map' : '☰ List'}
                </button>
              ))}
            </div>
          </div>

          {view === 'map' && (
            <div style={{ position: 'relative', flex: 1 }}>
              <div className="map-wrap">
                <MapContainer center={homePos} zoom={13} style={{ height: '100%', width: '100%' }}>
                  <TileLayer
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    attribution='© OpenStreetMap contributors'
                  />
                  <FitBounds points={routeLine} />
                  <Polyline positions={routeLine} color="#6366f1" weight={3} opacity={0.7} dashArray="6,4" />
                  <Marker position={homePos} icon={homeIcon}>
                    <Popup><b>{agent.name}</b><br />Home base</Popup>
                  </Marker>
                  {visits.map(v =>
                    v.latitude && v.longitude ? (
                      <Marker
                        key={v.visitId}
                        position={[v.latitude, v.longitude]}
                        icon={stopIcon(v.routeOrder, v.status === 'COMPLETED')}
                        eventHandlers={{ click: () => onVisitClick(v) }}
                      >
                        <Popup>
                          <b>Stop {v.routeOrder}: {v.borrowerName}</b><br />
                          {v.address}<br />
                          DPD: {v.dpd} · ₹{Number(v.outstandingAmount).toLocaleString('en-IN')}<br />
                          {v.distanceFromPrevious && (
                            <span>{v.distanceFromPrevious} km · {Math.round(v.estimatedTravelMinutes)} min</span>
                          )}
                        </Popup>
                      </Marker>
                    ) : null
                  )}
                </MapContainer>
              </div>
              <div className="map-hint">Tap any pin to see details</div>
            </div>
          )}

          {view === 'list' && (
            <div className="content">
              {visits.map(v => (
                <div key={v.visitId} className="card" onClick={() => onVisitClick(v)}>
                  <div className="visit-row">
                    <div
                      className="stop-badge"
                      style={{ background: v.status === 'COMPLETED' ? '#10b981' : '#6366f1' }}
                    >
                      {v.status === 'COMPLETED' ? '✓' : v.routeOrder}
                    </div>
                    <div style={{ flex: 1 }}>
                      <div className="visit-name">{v.borrowerName}</div>
                      <div className="visit-addr">{v.address}</div>
                      <div className="chips-row">
                        {statusChip(v.status)}
                        {v.hasPendingLegalNotice && <span className="chip chip-legal">Legal</span>}
                        {v.distanceFromPrevious != null && (
                          <span className="chip chip-skipped">{v.distanceFromPrevious} km · {Math.round(v.estimatedTravelMinutes)}min</span>
                        )}
                        {v.agentMatchScore != null && (
                          <span className="chip chip-priority">Match {Math.round(v.agentMatchScore * 100)}%</span>
                        )}
                      </div>
                    </div>
                    <div style={{ color: '#94a3b8', fontSize: 18 }}>›</div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  )
}

// ─── Screen 3: Visit Detail ───────────────────────────────────────────────────

function VisitDetail({ visit: initialVisit, agent, onBack, onOutcome, onRouteUpdated }) {
  const [visit, setVisit]           = useState(initialVisit)
  const [starting, setStarting]     = useState(false)
  const [deferring, setDeferring]   = useState(false)
  const [showDefer, setShowDefer]   = useState(false)   // show time-window picker
  const [windowStart, setWinStart]  = useState('')
  const [windowEnd, setWinEnd]      = useState('')

  const startVisit = async () => {
    setStarting(true)
    const res = await fetch(`${API}/api/visits/${visit.visitId}/start`, { method: 'PUT' })
    if (res.ok) setVisit(await res.json())
    setStarting(false)
  }

  const callReoptimise = async (lat, lng) => {
    const today = new Date().toISOString().split('T')[0]
    const body = {
      agentId:          agent.id,
      date:             today,
      deferVisitId:     visit.visitId,
      currentLat:       lat,
      currentLng:       lng,
      visitWindowStart: windowStart || null,
      visitWindowEnd:   windowEnd   || null,
    }
    const res = await fetch(`${API}/api/routes/reoptimise`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    })
    setDeferring(false)
    if (res.ok) onRouteUpdated(await res.json())
    else alert('Could not reoptimise route. Try again.')
  }

  const deferVisit = () => {
    setDeferring(true)
    navigator.geolocation.getCurrentPosition(
      pos => callReoptimise(pos.coords.latitude, pos.coords.longitude),
      ()  => callReoptimise(agent.homeLatitude, agent.homeLongitude),
      { timeout: 8000 }
    )
  }

  const canStart = visit.status === 'PENDING'
  const canLog   = visit.status === 'IN_PROGRESS' || visit.status === 'PENDING'
  const isDone   = visit.status === 'COMPLETED' || visit.status === 'SKIPPED'

  return (
    <div className="screen">
      <div className="topbar">
        <div className="topbar-row">
          <button className="back-btn" onClick={onBack}>‹</button>
          <div style={{ flex: 1 }}>
            <div className="topbar-title">{visit.borrowerName}</div>
            <div className="topbar-sub" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              Stop #{visit.routeOrder} · {statusChip(visit.status)}
            </div>
          </div>
        </div>
      </div>

      <div className="content">
        {visit.hasPendingLegalNotice && (
          <div className="alert alert-danger">
            ⚠️ Legal Notice — must be served today
          </div>
        )}

        <div className="card no-cursor">
          <div className="detail-group-title">Account Info</div>
          <div className="detail-row">
            <span className="detail-lbl">Loan ID</span>
            <span className="detail-val">{visit.loanId}</span>
          </div>
          <div className="detail-row">
            <span className="detail-lbl">Phone</span>
            <span className="detail-val">{visit.borrowerPhone}</span>
          </div>
          <div className="detail-row">
            <span className="detail-lbl">Address</span>
            <span className="detail-val">{visit.address}</span>
          </div>
          <div className="detail-row">
            <span className="detail-lbl">Outstanding</span>
            <span className="detail-val">{fmt(visit.outstandingAmount)}</span>
          </div>
          <div className="detail-row">
            <span className="detail-lbl">DPD</span>
            <span className="detail-val" style={{ color: visit.dpd >= 60 ? '#ef4444' : 'inherit' }}>
              {visit.dpd} days
            </span>
          </div>
          {visit.brokenPtpCount > 0 && (
            <div className="detail-row">
              <span className="detail-lbl">Broken PTPs</span>
              <span className="detail-val">{visit.brokenPtpCount}</span>
            </div>
          )}
        </div>

        <div className="card no-cursor">
          <div className="detail-group-title">Route Info</div>
          <div className="detail-row">
            <span className="detail-lbl">Priority Score</span>
            <div className="score-wrap" style={{ flex: 1, justifyContent: 'flex-end' }}>
              <div className="score-track">
                <div className="score-fill" style={{ width: `${(visit.priorityScore || 0) * 100}%`, background: '#6366f1' }} />
              </div>
              <span className="detail-val" style={{ minWidth: 36 }}>{Math.round((visit.priorityScore || 0) * 100)}%</span>
            </div>
          </div>
          {visit.agentMatchScore != null && (
            <div className="detail-row">
              <span className="detail-lbl">Agent Match</span>
              <div className="score-wrap" style={{ flex: 1, justifyContent: 'flex-end' }}>
                <div className="score-track">
                  <div className="score-fill" style={{ width: `${visit.agentMatchScore * 100}%`, background: '#10b981' }} />
                </div>
                <span className="detail-val" style={{ minWidth: 36 }}>{Math.round(visit.agentMatchScore * 100)}%</span>
              </div>
            </div>
          )}
          {visit.estimatedTravelMinutes != null && (
            <div className="detail-row">
              <span className="detail-lbl">Travel time</span>
              <span className="detail-val">{Math.round(visit.estimatedTravelMinutes)} min</span>
            </div>
          )}
        </div>

        {isDone && visit.outcomeType && (
          <div className="card no-cursor">
            <div className="outcome-badge">✓ Outcome Recorded</div>
            <div className="detail-row">
              <span className="detail-lbl">Result</span>
              <span className="detail-val">{visit.outcomeType.replace(/_/g, ' ')}</span>
            </div>
            {visit.amountCollected && (
              <div className="detail-row">
                <span className="detail-lbl">Collected</span>
                <span className="detail-val">{fmt(visit.amountCollected)}</span>
              </div>
            )}
            {visit.notes && (
              <div className="detail-row">
                <span className="detail-lbl">Notes</span>
                <span className="detail-val">{visit.notes}</span>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Borrower time window badge — shown if already deferred with a window */}
      {visit.visitWindowStart && (
        <div className="content" style={{ paddingTop: 0, paddingBottom: 0 }}>
          <div className="alert alert-warning">
            🕐 Borrower available {visit.visitWindowStart} – {visit.visitWindowEnd || '…'}
          </div>
        </div>
      )}

      {/* Defer / time-window picker */}
      {!isDone && showDefer && (
        <div className="content" style={{ paddingTop: 0 }}>
          <div className="card no-cursor">
            <div className="detail-group-title">When is the borrower available?</div>
            <div style={{ display: 'flex', gap: 10, marginBottom: 12 }}>
              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label className="form-label">From</label>
                <input className="form-input" type="time" value={windowStart}
                  onChange={e => setWinStart(e.target.value)} />
              </div>
              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label className="form-label">To</label>
                <input className="form-input" type="time" value={windowEnd}
                  onChange={e => setWinEnd(e.target.value)} />
              </div>
            </div>
            <p style={{ fontSize: 12, color: 'var(--text-2)', marginBottom: 12, lineHeight: 1.5 }}>
              Leave blank to defer to end of day. With a time window the route will be resequenced to arrive during the borrower's available slot.
            </p>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn btn-primary" style={{ flex: 1, marginBottom: 0 }}
                onClick={deferVisit} disabled={deferring}>
                {deferring ? 'Reoptimising…' : '🔄 Confirm & Reoptimise'}
              </button>
              <button className="btn btn-ghost" style={{ flex: 0, padding: '15px 18px', marginBottom: 0 }}
                onClick={() => setShowDefer(false)}>
                ✕
              </button>
            </div>
          </div>
        </div>
      )}

      {!isDone && (
        <div className="btn-wrap">
          {canStart && (
            <button className="btn btn-outline" onClick={startVisit} disabled={starting}>
              {starting ? 'Starting…' : '▶ Start Visit'}
            </button>
          )}
          {canLog && (
            <button className="btn btn-primary" onClick={() => onOutcome(visit)}>
              📋 Log Outcome
            </button>
          )}
          {!showDefer && (
            <button className="btn btn-ghost" onClick={() => setShowDefer(true)}
              style={{ color: '#f59e0b' }}>
              🔄 Borrower Unavailable / Reschedule
            </button>
          )}
        </div>
      )}
    </div>
  )
}

// ─── Screen 4: Log Outcome ────────────────────────────────────────────────────

const OUTCOMES = [
  'PAID_FULL', 'PAID_PARTIAL', 'PTP',
  'NOT_HOME', 'REFUSED', 'WRONG_ADDRESS',
  'ALREADY_SETTLED', 'CONTACT_LATER',
  'LEGAL_NOTICE_DELIVERED', 'DECEASED',
]

function LogOutcome({ visit, onBack, onDone }) {
  const [outcome, setOutcome]   = useState('')
  const [amount, setAmount]     = useState('')
  const [ptpDate, setPtpDate]   = useState('')
  const [notes, setNotes]       = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [gpsStatus, setGpsStatus]   = useState('idle')
  const [coords, setCoords]         = useState(null)

  const getGPS = () => {
    setGpsStatus('getting')
    navigator.geolocation.getCurrentPosition(
      pos => { setCoords({ lat: pos.coords.latitude, lng: pos.coords.longitude }); setGpsStatus('got') },
      ()  => setGpsStatus('error'),
      { timeout: 10000 }
    )
  }

  const submit = async () => {
    if (!outcome) return alert('Please select an outcome')
    setSubmitting(true)
    const res = await fetch(`${API}/api/visits/${visit.visitId}/outcome`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        outcome,
        notes: notes || null,
        amountCollected:  amount ? parseFloat(amount) : null,
        ptpDate:          ptpDate || null,
        visitLatitude:    coords?.lat ?? null,
        visitLongitude:   coords?.lng ?? null,
      }),
    })
    if (res.ok) onDone(await res.json())
    else alert('Failed to submit. Try again.')
    setSubmitting(false)
  }

  const needsAmount = ['PAID_FULL', 'PAID_PARTIAL'].includes(outcome)
  const needsPtp    = outcome === 'PTP'

  return (
    <div className="screen">
      <div className="topbar">
        <div className="topbar-row">
          <button className="back-btn" onClick={onBack}>‹</button>
          <div>
            <div className="topbar-title">Log Outcome</div>
            <div className="topbar-sub">{visit.borrowerName}</div>
          </div>
        </div>
      </div>

      <div className="content">
        <div className="card no-cursor">
          <div className="detail-group-title">What happened?</div>
          <div className="outcome-grid">
            {OUTCOMES.map(o => (
              <div
                key={o}
                className={`outcome-opt${outcome === o ? ' selected' : ''}`}
                onClick={() => setOutcome(o)}
              >
                {o.replace(/_/g, ' ')}
              </div>
            ))}
          </div>

          {needsAmount && (
            <div className="form-group">
              <label className="form-label">Amount Collected (₹)</label>
              <input
                className="form-input"
                type="number"
                placeholder="e.g. 15000"
                value={amount}
                onChange={e => setAmount(e.target.value)}
              />
            </div>
          )}

          {needsPtp && (
            <div className="form-group">
              <label className="form-label">PTP Date</label>
              <input
                className="form-input"
                type="date"
                value={ptpDate}
                onChange={e => setPtpDate(e.target.value)}
              />
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Notes (optional)</label>
            <textarea
              className="form-input"
              rows={3}
              placeholder="Any additional notes…"
              value={notes}
              onChange={e => setNotes(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label">GPS Location</label>
            {gpsStatus === 'idle' && (
              <button className="btn btn-outline" onClick={getGPS}>📍 Capture GPS</button>
            )}
            {gpsStatus === 'getting' && <Spinner text="Getting location…" />}
            {gpsStatus === 'got' && (
              <div className="gps-pill gps-got">
                📍 {coords.lat.toFixed(5)}, {coords.lng.toFixed(5)}
              </div>
            )}
            {gpsStatus === 'error' && (
              <div className="gps-pill gps-err">
                ✕ Could not get GPS. Proceeding without location.
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="btn-wrap">
        <button className="btn btn-primary" onClick={submit} disabled={submitting || !outcome}>
          {submitting ? 'Submitting…' : '✓ Submit Outcome'}
        </button>
        <button className="btn btn-ghost" onClick={onBack}>Cancel</button>
      </div>
    </div>
  )
}

// ─── Root ─────────────────────────────────────────────────────────────────────

export default function App() {
  const [screen, setScreen]   = useState('agents')
  const [agent, setAgent]     = useState(null)
  const [visit, setVisit]     = useState(null)
  const [visits, setVisits]   = useState(null)  // preloaded after reoptimise

  if (screen === 'agents')
    return <AgentSelect onSelect={a => { setAgent(a); setVisits(null); setScreen('route') }} />

  if (screen === 'route')
    return (
      <RouteList
        agent={agent}
        preloadedVisits={visits}
        onBack={() => setScreen('agents')}
        onVisitClick={v => { setVisit(v); setScreen('detail') }}
      />
    )

  if (screen === 'detail')
    return (
      <VisitDetail
        visit={visit}
        agent={agent}
        onBack={() => setScreen('route')}
        onOutcome={v => { setVisit(v); setScreen('outcome') }}
        onRouteUpdated={updated => { setVisits(updated); setScreen('route') }}
      />
    )

  if (screen === 'outcome')
    return <LogOutcome visit={visit} onBack={() => setScreen('detail')} onDone={updated => { setVisit(updated); setScreen('detail') }} />
}
